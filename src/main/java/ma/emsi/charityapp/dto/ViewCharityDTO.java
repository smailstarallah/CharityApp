package ma.emsi.charityapp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.emsi.charityapp.entities.*;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewCharityDTO {
    private String titre;
    private String description;
    private Date dateDebut;
    private String localisation;
    private Double objectifDeFinancement;
    private Double fondsActuels;
    private String categorie;
    private Set<Media> media;
    private String nomorganization;
    private String contactPrincipalOrg;
    private String adresse;
    private byte[] logo;

    public ViewCharityDTO(CharityAction charityAction) {
        this.titre = charityAction.getTitre();
        this.description = charityAction.getDescription();
        this.dateDebut = charityAction.getDateDebut();
        this.localisation = charityAction.getLocalisation();
        this.objectifDeFinancement = charityAction.getObjectifDeFinancement();
        this.fondsActuels = charityAction.getFondsActuels();
        this.categorie = charityAction.getCategorie();
        this.media = charityAction.getMedia();
        Organization org = charityAction.getOrganization();
        if (org != null) {
            this.nomorganization = org.getNom();
            this.contactPrincipalOrg = org.getContactPrincipal();
            this.adresse = org.getAdresseLegale();
            this.logo = org.getLogo();
        }
    }
}
