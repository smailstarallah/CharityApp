package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@lombok.Data
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.NoArgsConstructor
public class CharityAction {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String titre;
    private String description;
    private Date date;
    private String localisation;
    private Double objectifDeFinancement;
    private Double fondsActuels;
    private String Categorie;
    @OneToMany
    private Set<Media> media;
    @OneToMany
    private Set<Donation> donations;
    @ManyToOne
    private Organization organization;

    @OneToMany
    private Set<RegularUser> participants;
}
