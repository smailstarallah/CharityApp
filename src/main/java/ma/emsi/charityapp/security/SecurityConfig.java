package ma.emsi.charityapp.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ma.emsi.charityapp.security.JwtAuthenticationEntryPoint;
import ma.emsi.charityapp.security.JwtRequestFilter;

import java.util.Arrays;

@Configuration // Indique à Spring que cette classe fournit des configurations de beans
@EnableWebSecurity // Active la sécurité web dans l'application
@EnableMethodSecurity // Permet d'utiliser @PreAuthorize, @PostAuthorize, etc. sur les méthodes
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthEntryPoint; // Gère les erreurs d'authentication (401)

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Notre filtre personnalisé pour JWT

    /**
     * Configure les règles de sécurité HTTP
     * @param http L'objet HttpSecurity à configurer
     * @return La chaîne de filtres de sécurité configurée
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configuration CORS (Cross-Origin Resource Sharing)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Désactive CSRF (Cross-Site Request Forgery) car nous utilisons des tokens JWT
                // Pour les applications avec formulaires traditionnels, gardez CSRF activé
                .csrf(AbstractHttpConfigurer::disable)

                // Configuration du point d'entrée pour les erreurs d'authentification
                // Ce handler sera appelé lorsqu'un utilisateur non authentifié essaie d'accéder à une ressource protégée
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                )

                // Nous n'utilisons pas de sessions, car JWT est "stateless" (sans état)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configuration des règles d'autorisation pour les différentes URL
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques, accessibles sans authentification
                        .requestMatchers(
                                "/api/auth/**",
                                "/scripts/**",
                                "/api/regularusers/getcharityAction/**",
                                "/api/public/**",
                                "/api/regularusers/getallcharites",
                                "/api/user-actions/getall",
                                "/api/superadmin/login",
                                "/api/superadmin/save",
                                "/api/superadmin/all-organizations",
                                "/api/superadmin/register", // J'ai gardé ce chemin tel quel, vérifiez s'il doit commencer par "/"
                                "/api/regularusers/save", // J'ai gardé ce chemin tel quel, vérifiez s'il doit commencer par "/"
                                // Swagger UI v3 (OpenAPI)
                                "/v3/api-docs/**",      // Ajouté pour la spécification OpenAPI
                                "/swagger-ui/**",       // Déjà présent, pour les ressources UI
                                "/swagger-ui.html",
                                "/api/organizations/all"// Ajouté pour la page principale de Swagger UI
                        ).permitAll()
                        // Routes réservées à l'administrateur du site
                        .requestMatchers("/api/site-admin/**").hasAuthority("ROLE_SITE_ADMIN")

                        // Routes pour les administrateurs d'application
                        .requestMatchers("/api/app-admin/**").hasAuthority("ROLE_APP_ADMIN")

                        // Routes pour les utilisateurs normaux (et les rôles supérieurs)
                        .requestMatchers("/api/user/**").hasAnyAuthority("ROLE_REGULAR_USER", "ROLE_APP_ADMIN", "ROLE_SITE_ADMIN")

                        // Toutes les autres routes nécessitent une authentification
                        .anyRequest().authenticated()
                );

        // Ajoute notre filtre JWT personnalisé avant le filtre standard de Spring
        // Cela permet de valider le token JWT avant que Spring ne vérifie l'authentification
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure les paramètres CORS (Cross-Origin Resource Sharing)
     * Cela permet à votre API d'être appelée depuis un domaine différent (ex: votre front React)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Domaines autorisés (en production, limitez cette liste)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // En-têtes autorisés
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Expose l'en-tête Authorization dans la réponse (utile pour le client)
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // Autorise l'envoi des cookies (important si vous utilisez des cookies)
        configuration.setAllowCredentials(true);

        // Durée de mise en cache des résultats du preflight (en secondes)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applique cette config à toutes les routes
        return source;
    }

    /**
     * Encodeur de mot de passe pour le stockage sécurisé
     * BCrypt est recommandé car il intègre un "salt" automatique et est adaptatif
     * Alternative: Argon2PasswordEncoder pour une sécurité encore meilleure mais plus coûteuse en ressources
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Vous pouvez ajuster la "force" en passant un paramètre: new BCryptPasswordEncoder(12)
        // Plus le nombre est élevé, plus le hachage est sécurisé mais lent
        return new BCryptPasswordEncoder();
    }

    /**
     * Gestionnaire d'authentification utilisé pour valider les identifiants
     * Spring Boot l'configure automatiquement en coulisses
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
