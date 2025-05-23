package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@lombok.Data
@lombok.NoArgsConstructor
@DiscriminatorValue("SUPER_ADMIN")
@EqualsAndHashCode(callSuper=false)
public class SuperAdmin extends Users {
    @JsonIgnore
    @OneToMany(mappedBy = "superAdmin")
    private Set<Organization> organizations;

    public SuperAdmin(String nom, String preNom, String email, String telephone, Date dateNaissance, String password) {
        super(nom, preNom, email, telephone, dateNaissance, password);
        organizations = new HashSet<>();
    }

}
