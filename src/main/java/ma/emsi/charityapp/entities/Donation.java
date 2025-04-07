package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Entity
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private Double montante;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "regular_user_id")
    @EqualsAndHashCode.Exclude
    private RegularUser rUser;
    @ManyToOne
    private CharityAction charityAction;
}
