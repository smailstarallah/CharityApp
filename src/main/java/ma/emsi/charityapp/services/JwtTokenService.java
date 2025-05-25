package ma.emsi.charityapp.services;

import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;
public interface JwtTokenService {

    /**
     * Extrait le nom d'utilisateur du token JWT
     *
     * @param token Le token JWT
     * @return Le nom d'utilisateur extrait
     */
    String extractUsername(String token);

    /**
     * Extrait la date d'expiration du token JWT
     *
     * @param token Le token JWT
     * @return La date d'expiration
     */
    Date extractExpiration(String token);

    /**
     * Méthode générique pour extraire une claim spécifique du token
     *
     * @param token Le token JWT
     * @param claimsResolver Une fonction qui extrait la claim voulue
     * @return La claim extraite
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Extrait le rôle d'accès (AccessRole) du token JWT
     *
     * @param token Le token JWT
     * @return Le rôle d'accès extrait
     */
    String extractAccessRole(String token);

    /**
     * Génère un token JWT pour un utilisateur
     *
     * @param userDetails Les détails de l'utilisateur
     * @return Le token JWT généré
     */
    String generateToken(UserDetails userDetails);

    /**
     * Génère un token JWT avec des claims supplémentaires
     *
     * @param extraClaims Les claims supplémentaires à inclure
     * @param userDetails Les détails de l'utilisateur
     * @return Le token JWT généré
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Génère un token de rafraîchissement pour un utilisateur
     *
     * @param userDetails Les détails de l'utilisateur
     * @return Le token de rafraîchissement
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Valide un token JWT pour un utilisateur spécifique
     *
     * @param token Le token JWT à valider
     * @param userDetails Les détails de l'utilisateur
     * @return true si le token est valide pour cet utilisateur, false sinon
     */
    Boolean validateToken(String token, UserDetails userDetails);

    /**
     * Vérifie si un token est structurellement valide et non expiré
     *
     * @param token Le token JWT à vérifier
     * @return true si le token est valide, false sinon
     */
    boolean isTokenValid(String token);

}
