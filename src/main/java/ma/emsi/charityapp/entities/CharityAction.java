package ma.emsi.charityapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import ma.emsi.charityapp.validation.Unique;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.Set;

@Entity
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class CharityAction {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @NotBlank(message = "Le titre est obligatoire")
    @Unique(fieldName = "titre", entity = CharityAction.class, message = "Le titre doit être unique")
    @Column(nullable = false, unique = true)
    private String titre;
    @Column(nullable = false)
    @NotBlank(message = "La description est obligatoire")
    private String description;
    @Column(nullable = false)
    @CreationTimestamp
    private Date date;
    @Column(nullable = false)
    @NotBlank(message = "La Localisation de début est obligatoire")
    private String localisation;
    @NotNull(message = "L'objectif de l'action est obligatoire")
    private Double objectifDeFinancement;
    private Double fondsActuels;
    private String categorie;
    @OneToMany
    @JsonIgnore
    private Set<Media> media;
    @OneToMany
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<Donation> donations;
    @OneToMany
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<RegularUser> participants;
    @ManyToOne
    @JoinColumn(name = "organization_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Organization organization;

    @Override
    public String toString() {
        return "CharityAction{" +
                "Id=" + Id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", localisation='" + localisation + '\'' +
                ", objectifDeFinancement=" + objectifDeFinancement +
                ", fondsActuels=" + fondsActuels +
                ", Categorie='" + categorie + '\'' +
                ", media=" + (media != null ? media.stream().map(Media::getId).toList() : "null") +
                ", donations=" + (donations != null ? donations.stream().map(Donation::getId).toList() : "null") +
//                ", organization=" + organization.getNom() +
//                ", participants=" + participants.stream().map(RegularUser::getId).toString() +
                '}';
    }
}
