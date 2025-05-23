package ma.emsi.charityapp.controllers;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.charityapp.dto.MissionDescriptionRequest;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.services.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("")
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        try {
            log.info("Fetching all organizations ");
            return ResponseEntity.ok(organizationService.getAllOrganizations());
        } catch (Exception e) {
            log.error("Error fetching organizations: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}/mission")
    public ResponseEntity<Organization> addMissionDescription(@PathVariable Long id, @Valid @RequestBody MissionDescriptionRequest mission) {

        if (id == null || id <= 0) {
            System.out.println("Invalid ID ADD MISSION DESCRIPTION CONTROLLER");
            return ResponseEntity.badRequest().build();
        }

        Organization updatedOrg;
        try {
            updatedOrg = organizationService.addMissionDescription(id, mission.getMissionDescription());
            return ResponseEntity.ok(updatedOrg);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable Long id) {
        try {
            Organization organization = organizationService.getOrganizationById(id);
            return ResponseEntity.ok(organization);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Organization> updateOrganization(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("adresseLegale") String adresseLegale,
            @RequestParam("identifiantFiscal") String identifiantFiscal,
            @RequestParam("contactPrincipal") String contactPrincipal,
            @RequestParam("missionDescription") String missionDescription,
            @RequestParam(value = "logo", required = false) MultipartFile file) {
        try {
            Organization organization = new Organization();
            organization.setNom(nom);
            organization.setAdresseLegale(adresseLegale);
            organization.setIdentifiantFiscal(identifiantFiscal);
            organization.setContactPrincipal(contactPrincipal);
            organization.setMissionDescription(missionDescription);

            organizationService.updateOrganization(id, organization, file);
            System.out.println("Organization updated successfully");
            return ResponseEntity.ok(organization);
        } catch (EntityNotFoundException e) {
            System.out.println("Organization not found: " + e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (IOException e) {
            System.out.println("Error updating organization: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
