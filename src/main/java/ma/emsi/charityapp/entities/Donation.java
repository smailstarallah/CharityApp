package ma.emsi.charityapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 1, message = "Le montant doit être supérieur à 0")
    private Double montante;
    @CreationTimestamp
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regular_user_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private RegularUser rUser;
    @ManyToOne
    @JoinColumn(name = "charity_action_id")
    private CharityAction charityAction;

    public String toString() {
        return "Donation{" +
                "Id=" + Id +
                ", montante=" + montante +
                ", date=" + date +
                ", rUser=" + rUser.getId() +
                ", charityAction=" + charityAction +
                '}';
    }
}
