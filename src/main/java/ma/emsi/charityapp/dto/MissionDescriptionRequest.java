package ma.emsi.charityapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class MissionDescriptionRequest {
    @NotBlank(message = "La description de la mission ne peut pas être vide")
    private String missionDescription;
}
