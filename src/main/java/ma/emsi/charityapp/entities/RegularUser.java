package ma.emsi.charityapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import ma.emsi.charityapp.Enum.UserType;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;


@EqualsAndHashCode(callSuper = true)
@Entity
@lombok.Data
@lombok.NoArgsConstructor
@DiscriminatorValue("REGULAR")

public class RegularUser extends Users {

    UserType type;
    @JsonIgnore
    @OneToMany(mappedBy = "rUser", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<Donation> donationHistory;
    @OneToMany(mappedBy = "rUser", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Organization> organizations;
    @ManyToOne
    @JoinColumn(name = "charityAction_id")
    @JsonIgnore
    private CharityAction charityAction;

    public RegularUser(String nom, String preNom, String email, String telephone, Date dateNaissance, String password, UserType type) {
        super(nom, preNom, email, telephone, dateNaissance, password);
        this.type = type;
    }
}
