package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import ma.emsi.charityapp.Enum.OrganizationStatus;

import java.util.Set;

@Entity
@lombok.Data
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.NoArgsConstructor
public class Organization {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String nom;
    private String adresseLegale;
    private String identifiantFiscal;
    private String contactPrincipal;
    private String logo;
    private String missionDescription;
    private OrganizationStatus status;
    @ManyToOne
    @JoinColumn(name = "regular_user_id")
    private RegularUser rUser;
    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;
    @OneToMany(mappedBy = "organization")
    private Set<CharityAction> charityActions;
    }
