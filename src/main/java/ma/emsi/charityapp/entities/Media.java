package ma.emsi.charityapp.entities;

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
    private CharityAction charityAction;
}
