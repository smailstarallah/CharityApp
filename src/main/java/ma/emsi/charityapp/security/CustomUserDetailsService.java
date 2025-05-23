package ma.emsi.charityapp.security;

import ma.emsi.charityapp.entities.Users;
import ma.emsi.charityapp.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service qui charge les détails d'un utilisateur depuis la base de données
 * pour l'authentification et l'autorisation
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository userRepository;

    /**
     * Charge un utilisateur par son email (utilisé comme identifiant)
     * Cette méthode est appelée par Spring Security lors de l'authentification
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche de l'utilisateur dans la base de données par email
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        // Conversion des rôles de l'utilisateur au format Spring Security
        List<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user);

        // Construction d'un objet UserDetails de Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),       // Email comme identifiant
                user.getPassword(),    // Mot de passe
                authorities           // Ajout des autorités
        );
    }

    /**
     * Convertit le type d'utilisateur en objet SimpleGrantedAuthority
     * pour Spring Security
     */
    private List<SimpleGrantedAuthority> mapRolesToAuthorities(Users user) {
        try {
            // Vérifier si l'utilisateur est un superadmin (via instanceof ou autre méthode)
            if (isSuperAdmin(user)) {
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
            }
            
            // Pour les utilisateurs réguliers ou admin, utiliser le champ type
            String userType = getUserType(user);
            if (userType != null && !userType.isEmpty()) {
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType.toUpperCase()));
            }
            
            // Par défaut, attribuer un rôle utilisateur
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        } catch (Exception e) {
            // En cas d'erreur, attribuer un rôle par défaut
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
    
    /**
     * Détermine si l'utilisateur est un superadmin
     */
    private boolean isSuperAdmin(Users user) {
        // Implémentez la logique pour identifier un superadmin
        // Par exemple, vérifiez la classe ou un autre attribut
        return user.getClass().getSimpleName().equals("SuperAdmin");
    }
    
    /**
     * Obtient le type d'utilisateur (regularuser ou admin)
     */
    private String getUserType(Users user) {
        try {
            // Utilisation de la réflexion pour accéder au champ type
            // puisque ce champ n'existe que dans certaines sous-classes
            java.lang.reflect.Field typeField = user.getClass().getDeclaredField("type");
            typeField.setAccessible(true);
            return (String) typeField.get(user);
        } catch (Exception e) {
            return "USER"; // valeur par défaut
        }
    }
}
