package ma.emsi.charityapp.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import ma.emsi.charityapp.services.CharityActionService;
import ma.emsi.charityapp.services.OrganizationService;
import ma.emsi.charityapp.services.RegularUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/regularusers")
@Transactional
//@CrossOrigin(origins = "http://localhost:3000") // Allow requests from react app
public class RegularUserController {

    private static final Logger logger = LoggerFactory.getLogger(RegularUserController.class);
    private final RegularUserRepository regularUserRepository;

    RegularUserService regularUserService;
    OrganizationService organizationService;
    CharityActionService charityActionService;

    public RegularUserController(RegularUserService regularUserService, OrganizationService organizationService, CharityActionService charityActionService, RegularUserRepository regularUserRepository) {
        this.regularUserService = regularUserService;
        this.organizationService = organizationService;
        this.charityActionService = charityActionService;
        this.regularUserRepository = regularUserRepository;
    }

    @GetMapping("")
    List<RegularUser> getAll() {
//        RegularUser r = regularUserService.findById(1L);
//        r.setDonationHistory(new java.util.HashSet<>());
//        r.getDonationHistory().add(new Donation(null, 110.0, new java.util.Date(), null, null));
//        r.getDonationHistory().add(new Donation(null, 2120.0, new java.util.Date(), null, null));
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
    RegularUser save(@Valid @RequestBody RegularUser regularUser) {
        return regularUserService.save(regularUser);
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
    List<Donation> getDonationHistory(@PathVariable Long id) {
        if (regularUserService.findById(id) == null) {
            System.out.println("User not found");
        }
        return regularUserService.voirHistDesDons(id);
    }

    @PostMapping("/makeDonation/{userId}")
    String makeDonation(@RequestBody Donation donation, @PathVariable Long userId) {
        if (donation.getMontante() <= 0) {
            return "Le montant doit être supérieur à zéro.";
        }
        donation.setRUser(regularUserService.findById(userId));
        regularUserService.makeDonation(donation);
        return "done";
    }

    @PutMapping("/register/organization/{id}")
    @Transactional
    String registerOrganization(@PathVariable Long id, @RequestBody Organization org) {
        if ( id == null || id <= 0 || org == null) {
            throw new IllegalArgumentException("L'utilisateur ou l'organisation ne peut pas être nul");
        }
        try {
            System.out.println("Registering organization: " + org);
            System.out.println("User ID: " + id);
            regularUserService.registerOrganization(id, org);
            return "done";
        } catch (Exception e) {
            System.out.println("Error in registering organization: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/register/charity/{idUser}/{idOrg}")
    ResponseEntity<String> registerCharityAction(@PathVariable Long idUser, @PathVariable Long idOrg,@RequestBody @Valid CharityAction charityAction) {
        /*Validation insuffisante :
La vérification idUser == null || idOrg <= 0 || idOrg == null || idUser <= 0 || charityAction == null est correcte, mais elle ne valide pas :
Si charityAction a des champs obligatoires manquants (par exemple, via @NotNull).
Si RegularUser existe (la méthode regularUserService.findById peut retourner null ou lancer une exception).*/
        // verifier que l'admin est l'admin de l'organisation
        logger.debug("Requête pour enregistrer une action caritative pour l'utilisateur ID={} et l'organisation ID={}", idUser, idOrg);
        if ( idUser == null || idOrg <= 0 ||idOrg == null || idUser <= 0 || charityAction == null) {
            throw new IllegalArgumentException("L'utilisateur ou l'organisation ou la l'action ne peut pas être nul");
        }

        Long charityActionId = regularUserService.registerCharityAction(idUser, idOrg, charityAction);
        logger.info("Action caritative enregistrée pour l'utilisateur ID={}", idUser);
        if (charityActionId == null) {
            logger.error("Erreur lors de l'enregistrement de l'action caritative");
            return ResponseEntity.status(500).body("Erreur lors de l'enregistrement de l'action caritative");
        }
        if (charityActionService.existsById(charityActionId)) {
            logger.info("Action caritative enregistrée avec succès");
            regularUserRepository.updateCharityAction(idUser, charityActionId);
            return ResponseEntity.ok("Action caritative enregistrée avec succès");
        } else {
            logger.error("Erreur lors de l'enregistrement de l'action caritative");
            return ResponseEntity.status(500).body("Erreur lors de l'enregistrement de l'action caritative");
        }
    }

}
