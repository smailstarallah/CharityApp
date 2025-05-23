package ma.emsi.charityapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrganizationRegisterDTO {
        Long id;
        @NotNull
        private String nom;
        @NotNull
        private String adresseLegale;
        @NotNull
        private String identifiantFiscal;
        @NotNull
        private String contactPrincipal;
        private String missionDescription;
        private RegularUser rUser;
        public OrganizationRegisterDTO(Organization org){
            this.nom = org.getNom();
            this.adresseLegale = org.getAdresseLegale();
            this.identifiantFiscal = org.getIdentifiantFiscal();
            this.contactPrincipal = org.getContactPrincipal();
            this.missionDescription = org.getMissionDescription();
        }
}
