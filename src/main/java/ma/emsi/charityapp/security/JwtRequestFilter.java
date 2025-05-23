package ma.emsi.charityapp.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.emsi.charityapp.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre qui intercepte chaque requête HTTP pour vérifier et valider le token JWT
 */
@Component // Cette annotation indique que cette classe est un composant Spring qui sera automatiquement détecté
public class JwtRequestFilter extends OncePerRequestFilter {
    // OncePerRequestFilter garantit que ce filtre s'exécute une seule fois par requête

    @Autowired
    private JwtTokenService jwtTokenService; // Service qui gère les opérations JWT (création, validation, extraction des claims)

    @Autowired
    private CustomUserDetailsService userDetailsService; // Service qui charge les détails de l'utilisateur depuis la base de données

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // ÉTAPE 1: Extraire le token JWT de l'en-tête Authorization
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // ÉTAPE 2: Vérifier si le token est présent et au format "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // Extraire le token en enlevant le préfixe "Bearer "
            jwtToken = requestTokenHeader.substring(7);

            try {
                // ÉTAPE 3: Extraire le nom d'utilisateur du token
                username = jwtTokenService.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                // Si le token est vide ou null
                logger.error("Impossible d'obtenir le token JWT");
            } catch (ExpiredJwtException e) {
                // Si le token a dépassé sa date d'expiration
                logger.error("Le token JWT a expiré");
            } catch (MalformedJwtException e) {
                // Si le format du token est incorrect
                logger.error("Token JWT invalide");
            }
        } else {
            // Ce n'est pas une erreur, juste une requête sans token ou avec un format incorrect
            logger.debug("Le token JWT n'est pas au format Bearer");
        }

        // ÉTAPE 4: Valider le token et configurer l'authentification Spring Security
        // Deux conditions importantes:
        // 1. Nous avons réussi à extraire un username du token
        // 2. Aucune authentification n'est déjà présente dans le contexte de sécurité
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ÉTAPE 5: Charger les détails de l'utilisateur depuis la base de données
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ÉTAPE 6: Valider le token pour cet utilisateur
            // Vérifie si le token correspond à cet utilisateur et s'il n'est pas expiré
            if (jwtTokenService.validateToken(jwtToken, userDetails)) {

                // ÉTAPE 7: Si le token est valide, créer un objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,           // Principal (l'utilisateur authentifié)
                                null,                  // Credentials (null car déjà authentifié)
                                userDetails.getAuthorities() // Rôles/autorités de l'utilisateur
                        );

                // ÉTAPE 8: Ajouter des détails sur la requête (IP, session, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ÉTAPE 9: Définir l'authentification dans le contexte de sécurité
                // C'est cette ligne qui "connecte" effectivement l'utilisateur dans Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // À partir de ce moment, l'utilisateur est considéré comme authentifié
                // pour le reste de la chaîne de filtres et les contrôleurs
            }
        }

        // ÉTAPE 10: Continuer la chaîne de filtres
        // Cette étape est cruciale pour passer au filtre suivant ou au contrôleur
        filterChain.doFilter(request, response);
    }
}