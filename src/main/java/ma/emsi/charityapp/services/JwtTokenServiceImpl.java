package ma.emsi.charityapp.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implémentation du service de gestion des tokens JWT.
 */
@Slf4j
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Value("${jwt.secret:defaultSecretKeyWhichShouldBeVeryLongForSecurityReasons}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 heures par défaut
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 jours par défaut
    private long refreshExpiration;

    final RegularUserRepository userRepository;
    final SuperAdminService superAdminService;

    public JwtTokenServiceImpl(RegularUserRepository userRepository, SuperAdminService superAdminService) {
        this.userRepository = userRepository;
        this.superAdminService = superAdminService;
    }

    // Génère une clé de signature à partir du secret
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraire toutes les claims du token
    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
        return parser.parseClaimsJws(token).getBody();
    }

    // Vérifier si le token est expiré
    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return createToken(extraClaims, userDetails.getUsername(), jwtExpiration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), refreshExpiration);
    }

    // Créer le token JWT
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        try {
            String role = userRepository.findByEmail(subject).getType().toString();
            if (role != null) {
                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expiration))
                        .claim("AccessRole", "REGULAR_USER")
                        .claim("Role", userRepository.findByEmail(subject).getType().toString())
                        .claim("Id", userRepository.findByEmail(subject).getId())
                        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                        .compact();
            }
        } catch (Exception e) {
            System.out.println("il y'a exception");
        }
        log.error("Erreur lors de la création du token JWT :");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .claim("AccessRole", "SUPER_ADMIN")
                .claim("Id", superAdminService.findByEmail(subject).getId())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenValid(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build();
            parser.parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait le rôle d'accès (AccessRole) du token JWT
     *
     * @param token Le token JWT
     * @return Le rôle d'accès extrait
     */
    @Override
    public String extractAccessRole(String token) {
        return extractClaim(token, claims -> claims.get("AccessRole", String.class));
    }
}

