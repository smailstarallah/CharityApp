package ma.emsi.charityapp.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.emsi.charityapp.dto.OrganizationRegisterDTO;
import ma.emsi.charityapp.dto.ViewCharityDTO;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.services.CharityActionService;
import ma.emsi.charityapp.services.MediaService;
import ma.emsi.charityapp.services.OrganizationService;
import ma.emsi.charityapp.services.RegularUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/regularusers")
public class RegularUserController {

    private static final Logger logger = LoggerFactory.getLogger(RegularUserController.class);
    private final MediaService mediaService;

    RegularUserService regularUserService;
    OrganizationService organizationService;
    CharityActionService charityActionService;
    private final PasswordEncoder passwordEncoder; // Ajouter PasswordEncoder


    public RegularUserController(RegularUserService regularUserService, OrganizationService organizationService, CharityActionService charityActionService, PasswordEncoder passwordEncoder, MediaService mediaService) {
        this.regularUserService = regularUserService;
        this.organizationService = organizationService;
        this.charityActionService = charityActionService;
        this.passwordEncoder = passwordEncoder;
        this.mediaService = mediaService;
    }



    @GetMapping("/")
    String home() {
        return "home";
    }

    @GetMapping("/all")
    List<RegularUser> getAll() {
        return regularUserService.getAll();
    }

    @GetMapping("/{id}")
    RegularUser getById(@PathVariable Long id) {
        return regularUserService.findById(id);
    }

    @GetMapping("/email/{email}")
    RegularUser getByEmail(@PathVariable String email) {
        return regularUserService.findByEmail(email);
    }

    @DeleteMapping("/delete/{id}")
    String deleteById(@PathVariable Long id) {
        try {
            if (regularUserService.findById(id) == null) {
                return "user n'existe pas";
            }
            regularUserService.deleteById(id);
            return "done";
        } catch (Exception e) {
            return "error dans la suppression de User";
        }
    }

    @PostMapping("/save")
    ResponseEntity<String> save(@Valid @RequestBody RegularUser regularUser) {
        // Encoder le mot de passe avant de sauvegarder
        regularUser.setPassword(passwordEncoder.encode(regularUser.getPassword()));
        try {
            regularUserService.save(regularUser);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de l'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        logger.info("Utilisateur enregistré avec succès:");
        return ResponseEntity.ok("l'utilisateur a été enregistré avec succès");
    }

    @PutMapping("/update/{id}")
    RegularUser update(@PathVariable Long id, @Valid @RequestBody RegularUser regularUser) {
        RegularUser existingUser = regularUserService.findById(id);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        return regularUserService.update(id, regularUser);
    }

    @GetMapping("/donationHistory/{id}")
    ResponseEntity<List<Donation>> getDonationHistory(@PathVariable Long id) {
        if (regularUserService.findById(id) == null) {
            System.out.println("User not found");
        }

        List<Donation> donationHistory = regularUserService.voirHistDesDons(id);

        return ResponseEntity.ok(donationHistory);
    }


    ResponseEntity<String> makeDonation(
            Donation donation,
            Long userId,
            Long charityId) {
        logger.info("Requête pour faire un don de montant {} pour l'utilisateur ID={} et l'action caritative ID={}");

        try {

            if ( charityId <= 0 || userId <= 0 ) {
                throw new EntityNotFoundException("L'utilisateur ou l'action caritative");
            }
            System.out.println(charityId);
            RegularUser user = regularUserService.findById(userId);
            CharityAction charityAction = charityActionService.findById(charityId);

            donation.setCharityAction(charityAction);
            donation.setRUser(user);
            logger.info("Donation: {}", donation);
            Donation d = regularUserService.makeDonation(donation);
            logger.info("Donation {} made successfully for user ID={} and charity ID={}", d, userId, charityId);
            return ResponseEntity.ok("donation faite avec succès");
        }catch (EntityNotFoundException e) {
            logger.error("User or CharityAction not found: {}", e.getMessage());
            return ResponseEntity.status(404).body("User or CharityAction not found");
        } catch (Exception e) {
            logger.error("Error making donation: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error making donation");
        }
    }

    @PutMapping("/register/organization/{mail}")
    @Transactional
    public String registerOrganization(
            @PathVariable String mail,
            @RequestParam("nom") String nom,
            @RequestParam("adresseLegale") String adresseLegale,
            @RequestParam("identifiantFiscal") String identifiantFiscal,
            @RequestParam("contactPrincipal") String contactPrincipal,
            @RequestParam("missionDescription") String missionDescription,
            @RequestParam(value = "logo", required = false) MultipartFile file) {

        if (mail == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être nul");
        }
        logger.debug("Requête pour enregistrer une organisation pour l'utilisateur avec l'email: {}", mail);

        try {
            // Créer manuellement l'objet OrganizationRegisterDTO à partir des paramètres individuels
            OrganizationRegisterDTO org = new OrganizationRegisterDTO();
            org.setNom(nom);
            org.setAdresseLegale(adresseLegale);
            org.setIdentifiantFiscal(identifiantFiscal);
            org.setContactPrincipal(contactPrincipal);
            org.setMissionDescription(missionDescription);
            logger.debug("OrganizationRegisterDTO: {}", org);

            System.out.println("Registering organization: " + org);
            System.out.println("User ID: " + mail);
            RegularUser user = regularUserService.findByEmail(mail);
            logger.debug("RegularUser: {}", user);
            regularUserService.registerOrganization(user.getId(), org, file);
            logger.info("Organisation enregistrée avec succès pour l'utilisateur avec l'email: {}", mail);
            return "done";
        } catch (Exception e) {
            logger.error("Error in registering organization: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/register/charity/{mail}/{idOrg}")
    ResponseEntity<String> registerCharityAction(
            @PathVariable @Email @NotBlank String mail,
            @PathVariable @NotNull Long idOrg,
            @RequestPart("charityAction") @Valid CharityAction charityAction,
            @RequestPart(value = "media", required = false) MultipartFile[] mediaFiles) {


        Long idUser = regularUserService.findByEmail(mail).getId();
        logger.info("Requête pour enregistrer une action caritative pour l'utilisateur ID={} et l'organisation ID={}", idUser, idOrg);
        if ( idUser == null || idOrg <= 0  || idUser <= 0 || charityAction == null) {
            throw new IllegalArgumentException("L'utilisateur ou l'organisation ou la l'action ne peut pas être nul");
        }

        Long charityActionId = null;
        try {
            charityActionId = regularUserService.registerCharityAction(idUser, idOrg, charityAction, mediaFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Action caritative enregistrée pour l'utilisateur ID={}", idUser);
        if (charityActionId == null) {
            logger.error("Erreur lors de l'enregistrement de l'action caritative");
            return ResponseEntity.status(500).body("Erreur lors de l'enregistrement de l'action caritative");
        }

        return ResponseEntity.ok("Action caritative enregistrée avec succès");
    }

    @GetMapping("/organization/{email}")
    ResponseEntity<List<Organization>> getOrganizationsByUserId(@PathVariable @Email String email) {
        RegularUser user = regularUserService.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("L'utilisateur n'existe pas");
        }
        List<Organization> organizations = organizationService.findOrganizationByUser(user);
        if (organizations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/mesactions/{email}/{idOrg}")
    ResponseEntity<List<CharityAction>> getCharityActionsByUserId(
            @PathVariable @Email @NotBlank String email,
            @PathVariable @NotNull Long idOrg ){

        logger.info("Requête pour obtenir les actions caritatives de l'utilisateur avec l'email: {} et l'organisation ID={}", email, idOrg);
        RegularUser user = regularUserService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<CharityAction> charityActions = charityActionService.findCharityActionByUserAndOrganization(user.getId(), idOrg);

        if (charityActions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(charityActions);
    }

    @PutMapping("/update/charity/{id}")
    ResponseEntity<String> updateCharityAction(
            @PathVariable Long id,
            @RequestPart("charityAction") CharityAction charityAction,
            @RequestPart(value = "media", required = false) MultipartFile[] mediaFiles) {

        try {
            if(!charityActionService.existsById(id)){
                return ResponseEntity.status(404).body("Action caritative non trouvée");
            }

            charityAction.setId(id);

            charityActionService.updateCharityAction(id, charityAction, mediaFiles);

            return ResponseEntity.ok("Action caritative mise à jour avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'action caritative: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erreur lors de la mise à jour de l'action caritative");
        }
    }

    @DeleteMapping("/delete/Media/{id}")
    ResponseEntity<String> deleteMedia(@PathVariable @NotNull Long id) {
        try {
            if (!mediaService.existsById(id)) {
                return ResponseEntity.status(404).body("Media non trouvé");
            }
            mediaService.deleteMediaById(id);
            return ResponseEntity.ok("Media supprimé avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du media: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erreur lors de la suppression du media");
        }
    }

    @GetMapping("/getcharityAction/{id}")
    ResponseEntity<ViewCharityDTO> getCharityActionById(@PathVariable Long id) {
        CharityAction charityAction = charityActionService.findById(id);
        logger.info("charite action: {}", charityAction);
        if (charityAction == null) {
            return ResponseEntity.notFound().build();
        }
        ViewCharityDTO viewCharityDTO = new ViewCharityDTO(charityAction);
        return ResponseEntity.ok(viewCharityDTO);
    }

    @GetMapping("/getallcharites")
    ResponseEntity<List<ViewCharityDTO>> getAllCharityActions() {
        logger.info("Requête pour obtenir toutes les actions caritatives");
        List<ViewCharityDTO> charityActions = charityActionService.getAll();
        if (charityActions.isEmpty()) {
            logger.info("Aucune action caritative trouvée");
            return ResponseEntity.noContent().build();
        }
        logger.info("Actions caritatives trouvées :)");
        return ResponseEntity.ok(charityActions);
    }
}
