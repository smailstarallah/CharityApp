package ma.emsi.charityapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import ma.emsi.charityapp.Enum.OrganizationStatus;

import java.util.Set;

@Entity
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class Organization {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @NotNull
    private String nom;
    @NotNull
    private String adresseLegale;
    @NotNull
    private String identifiantFiscal;
    @NotNull
    private String contactPrincipal;
    private String logo;
    private String missionDescription;
    private OrganizationStatus status;
    @ManyToOne
    @JoinColumn(name = "regular_user_id")
    @JsonIgnore
    private RegularUser rUser;
    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    @JsonIgnore
    private SuperAdmin superAdmin;
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "organization")
    private Set<CharityAction> charityActions;

    @Override
    public String toString() {
        return "Organization{" +
                "Id=" + Id +
                ", nom='" + nom + '\'' +
                ", adresseLegale='" + adresseLegale + '\'' +
                ", identifiantFiscal='" + identifiantFiscal + '\'' +
                ", contactPrincipal='" + contactPrincipal + '\'' +
                ", logo='" + logo + '\'' +
                ", missionDescription='" + missionDescription + '\'' +
                ", status=" + status +
                ", rUser=" + (rUser != null ? rUser.getId() : null) +
                ", superAdmin=" + ( superAdmin != null ? superAdmin.getId() : null) +
                ", charityActions=" + (charityActions != null ? charityActions.stream().map(CharityAction::getId).toList() : "null") +
                '}';
    }
}
