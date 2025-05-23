package ma.emsi.charityapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import ma.emsi.charityapp.Enum.OrganizationStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashMap;
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
    private String paypalEmail;
    @Lob
    private byte[] logo;
    private String missionDescription;
    private OrganizationStatus status;
    @CreationTimestamp
    private Date date;
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
                ", missionDescription='" + missionDescription + '\'' +
                ", status=" + status +
                ", rUser=" + (rUser != null ? rUser.getId() : null) +
                ", superAdmin=" + ( superAdmin != null ? superAdmin.getId() : null) +
                ", charityActions=" + (charityActions != null ?
                charityActions.stream()
                        .map(c -> { HashMap<Long, String> map = new HashMap<>(); map.put(c.getId(), c.getTitre()); return map; })
                        .toList()
                : "null") +
                '}';
    }
}
