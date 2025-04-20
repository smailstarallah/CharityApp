package ma.emsi.charityapp.controllers;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import ma.emsi.charityapp.Enum.OrganizationStatus;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.services.OrganizationService;
import ma.emsi.charityapp.services.SuperAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    private final OrganizationService organizationService;
    SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService, OrganizationService organizationService) {
        this.superAdminService = superAdminService;
        this.organizationService = organizationService;
    }

    @GetMapping("")
    List<SuperAdmin> getAll() {
        return superAdminService.getAll();
    }

    @GetMapping("/{id}")
    SuperAdmin getById(@PathVariable Long id) {
        return superAdminService.findById(id);
    }

    @GetMapping("/email/{email}")
    SuperAdmin getByEmail(@PathVariable @Email String email) {
        return superAdminService.findByEmail(email);
    }

    @DeleteMapping("/delete/{id}")
    String deleteById(@PathVariable Long id) {
        try {
            if( superAdminService.findById(id) == null){
                return "not found";
            }
            superAdminService.deleteById(id);
            return "done";
        } catch (Exception e) {
            return "error dans la suppression de l'admin";
        }
    }

//    @PostMapping("/save")
//    SuperAdmin save(@Valid @RequestBody SuperAdmin superAdmin) {
////        int random = (int) (Math.random() * 10000000);
////        SuperAdmin superAdmin = new SuperAdmin("a", "a", "aaaa"+ (char)(random % 100) + ( (char) (int) (Math.random() * 100))  , random, new java.util.Date(1999, 4, 12), "a");
//        return superAdminService.save(superAdmin);
//    }

    @PostMapping("/save")
    SuperAdmin save(@Valid @RequestBody SuperAdmin regularUser) {
        return superAdminService.save(regularUser);
    }

    @PutMapping("/update/{id}")
    SuperAdmin update(@PathVariable Long id,@Valid @RequestBody SuperAdmin superAdmin) {
        return superAdminService.update(id, superAdmin);
    }

    @PutMapping("/update/{id}/approve/{orgId}")
    @Transactional
    String approveOrganization(@PathVariable Long id, @PathVariable Long orgId) {
        if (organizationService.findById(orgId).get().getStatus() == OrganizationStatus.Approved) {
            return "already approved";
        }
        if(id == null || orgId == null || id <= 0 || orgId <= 0) {
            return "id or orgId cannot be null";
        }
        superAdminService.approveOrganization(id, orgId);
        return "done";
    }
}