package ma.emsi.charityapp.controllers;

import ma.emsi.charityapp.repositories.UsersRepository;
import ma.emsi.charityapp.services.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import ma.emsi.charityapp.entities.Users;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;
    private final UsersRepository userService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenService jwtTokenService, UsersRepository userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    /**
     * Endpoint d'authentification (login)
     * Accepte username et password, retourne un token JWT si l'authentification réussit
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.get("username"),
                            loginRequest.get("password")
                    )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.get("username"));
            final String token = jwtTokenService.generateToken(userDetails);

            // Vérifier le rôle d'accès après la génération du token
            String accessRole = jwtTokenService.extractAccessRole(token);
            logger.info("Access Role: {}", accessRole);

            // Vérifier l'origine de la requête
            String origin = request.getHeader("Origin");
            logger.info("Request Origin: {}", origin);

            int port = request.getServerPort();
            String expectedOrigin = request.getScheme() + "://" + request.getServerName() + (port == 80 || port == 443 ? "" : ":" + port);
            boolean isSameOrigin = (origin == null || origin.equals(expectedOrigin));

            if (isSameOrigin && "SUPER_ADMIN".equalsIgnoreCase(accessRole)) {
                // Connexion autorisée pour super admin depuis la même origine
            } else if (!isSameOrigin && "REGULAR_USER".equalsIgnoreCase(accessRole)) {
                // Connexion autorisée pour regular user depuis une autre origine
            } else {
                logger.warn("Accès refusé : combinaison origine/rôle non autorisée");
                return ResponseEntity.status(403).body(Map.of("error", "Accès refusé : combinaison origine/rôle non autorisée"));
            }

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());

            logger.info("Authentification réussie pour l'utilisateur: {}", loginRequest.get("username"));
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            logger.error("Échec d'authentification pour l'utilisateur: {} - Identifiants invalides", loginRequest.get("username"));
            Map<String, String> response = new HashMap<>();
            response.put("error", "Identifiants invalides");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification pour l'utilisateur: {}", loginRequest.get("username"), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Une erreur s'est produite pendant l'authentification");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint d'inscription (register)
     * Crée un nouvel utilisateur dans la base de données
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        // Vérifier si l'utilisateur existe déjà
        if (userService.existsByEmail(user.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Nom d'utilisateur déjà utilisé");
            return ResponseEntity.badRequest().body(response);
        }

        // Enregistrer le nouvel utilisateur
        Users savedUser = userService.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Utilisateur enregistré avec succès");
        response.put("username", savedUser.getEmail());

        return ResponseEntity.ok(response);
    }
}

