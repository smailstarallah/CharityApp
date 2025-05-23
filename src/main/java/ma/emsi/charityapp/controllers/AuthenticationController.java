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
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        logger.debug("Tentative d'authentification pour l'utilisateur: {}", loginRequest.get("username"));

        try {
            logger.debug("Authentification via AuthenticationManager...");
            // Authentifie l'utilisateur avec Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.get("username"),
                            loginRequest.get("password")
                    )
            );
            logger.info("Authentification réussie pour l'utilisateur: {}", loginRequest.get("username"));

            // Si l'authentification réussit, génère un token JWT
            logger.debug("Chargement des détails utilisateur...");
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.get("username"));
            logger.debug("Génération du token JWT...");
            final String token = jwtTokenService.generateToken(userDetails);
            logger.debug("Token JWT généré avec succès");

            // Renvoie le token au client
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());

            logger.info("Connexion réussie et token envoyé pour: {}", userDetails.getUsername());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // En cas d'échec d'authentification
            logger.warn("Échec d'authentification pour l'utilisateur: {} - Identifiants invalides", loginRequest.get("username"));
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
