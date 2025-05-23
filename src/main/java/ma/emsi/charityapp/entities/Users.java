package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import ma.emsi.charityapp.validation.Unique;

import java.util.Date;

@lombok.Data
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Users {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @NotBlank(message = "Le prenom est obligatoire")
    private String preNom;
    @Column(unique = true,nullable = false)
    @NotBlank(message = "Le email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Unique(fieldName = "email", entity = Users.class, message = "L'email doit être unique")
    private String email;
    @Column(unique = true,nullable = false)
    @Pattern(
            regexp = "^(\\+212|0)([5-6-7])\\d{8}$",
            message = "Le numéro de téléphone doit être un numéro marocain valide"
    )
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Unique(fieldName = "telephone", entity = Users.class, message = "Le numéro de téléphone déjà utilisé par un autre utilisateur")
    private String telephone;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @Past(message = "La date de naissance doit être dans le passé")
    private Date dateNaissance;
    @NotBlank(message = "Le password est obligatoire")
    @Column(nullable = false)
    private String password;

    public Users(String nom, String preNom, String email, String telephone, Date dateNaissance, String password) {
        this.nom = nom;
        this.preNom = preNom;
        this.email = email;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.password = password;
    }
}
