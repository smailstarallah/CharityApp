package ma.emsi.charityapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import ma.emsi.charityapp.Enum.MediaType;

@Entity
@lombok.Data
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.NoArgsConstructor
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private MediaType type;
    private String url;
    @ManyToOne
    @JoinColumn(name = "charity_action_id")
    @JsonIgnore
    private CharityAction charityAction;
}
