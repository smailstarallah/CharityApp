package ma.emsi.charityapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.*;
import ma.emsi.charityapp.validation.Unique;

import java.util.Date;

@lombok.Data
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.NoArgsConstructor
@MappedSuperclass

public abstract class Users {
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
    @Unique(fieldName = "telephone", entity = Users.class, message = "Le numéro de téléphone doit être unique")
    private String telephone;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @Past(message = "La date de naissance doit être dans le passé")
    private Date dateNaissance;
    @NotBlank(message = "Le password est obligatoire")
    @Column(nullable = false)
    private String password;
}
