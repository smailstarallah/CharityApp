package ma.emsi.charityapp.security;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Ce composant gère les erreurs d'authentification (401 Unauthorized)
 * Il est appelé lorsqu'un utilisateur non authentifié tente d'accéder à une ressource protégée
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    /**
     * Cette méthode est invoquée lorsqu'un utilisateur tente d'accéder à une ressource protégée
     * sans fournir d'identifiants valides
     *
     * Elle renvoie une réponse d'erreur 401 (Unauthorized) au client
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Vous pouvez personnaliser le message d'erreur renvoyé
        String errorMessage = "Accès non autorisé. Authentification requise.";

        // Si l'erreur vient d'un token expiré, nous pouvons le spécifier
        if (request.getAttribute("expired") != null && (Boolean) request.getAttribute("expired")) {
            errorMessage = "Votre session a expiré. Veuillez vous reconnecter.";
        }

        // Configuration de la réponse d'erreur
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println("{ \"error\": \"" + errorMessage + "\" }");
    }
}