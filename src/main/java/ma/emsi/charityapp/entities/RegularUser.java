package ma.emsi.charityapp.entities;

import lombok.EqualsAndHashCode;
import ma.emsi.charityapp.Enum.UserType;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@lombok.Data
@lombok.NoArgsConstructor
public class RegularUser extends Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    UserType type;
    @OneToMany(mappedBy = "rUser")
    @EqualsAndHashCode.Exclude
    private Set<Donation> donationHistory;
    @OneToMany(mappedBy = "rUser")
    @EqualsAndHashCode.Exclude
    private Set<Organization> organizations;

    public RegularUser(String nom, String preNom, String email, String telephone, Date dateNaissance, String password, UserType type) {
        super(nom, preNom, email, telephone, dateNaissance, password);
        this.type = type;
    }
}
