package ma.emsi.charityapp.entities;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@lombok.Data
@lombok.NoArgsConstructor
public class SuperAdmin extends Users {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @OneToMany(mappedBy = "superAdmin")
    private Set<Organization> organizations;

    public SuperAdmin(String nom, String preNom, String email, String telephone, Date dateNaissance, String password) {
        super(nom, preNom, email, telephone, dateNaissance, password);
    }
}
