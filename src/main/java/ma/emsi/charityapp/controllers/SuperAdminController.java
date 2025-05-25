package ma.emsi.charityapp.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.services.OrganizationService;
import ma.emsi.charityapp.services.SuperAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    private final OrganizationService organizationService;
    SuperAdminService superAdminService;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminController(SuperAdminService superAdminService, OrganizationService organizationService, PasswordEncoder passwordEncoder) {
        this.superAdminService = superAdminService;
        this.organizationService = organizationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/id/{id}")
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

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<SuperAdmin> save(@Valid @RequestBody SuperAdmin user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            SuperAdmin savedUser = superAdminService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    SuperAdmin update(@PathVariable Long id,@Valid @RequestBody SuperAdmin superAdmin) {
        return superAdminService.update(id, superAdmin);
    }

    @PostMapping("/approve-organization/{orgId}")
    String approveOrganization(@PathVariable Long orgId, RedirectAttributes redirectAttributes) {
        if (organizationService.findById(orgId).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Organization not found");
            return "redirect:/api/superadmin/all-organizations";
        }
        superAdminService.approveOrganization(orgId);
        redirectAttributes.addFlashAttribute("success", "Organization approved successfully");
        return "redirect:/api/superadmin/all-organizations";
    }

    @PostMapping("/reject-organization/{orgId}")
    String rejectOrganization(@PathVariable Long orgId, RedirectAttributes redirectAttributes) {
        if (organizationService.findById(orgId).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Organization not found");
            return "redirect:/api/superadmin/all-organizations";
        }
        superAdminService.rejectOrganization(orgId);
        redirectAttributes.addFlashAttribute("success", "Organization rejected successfully");
        return "redirect:/api/superadmin/all-organizations";
    }

    @GetMapping("/all-organizations")
    String getAllOrganizations(Model model) {
        List<Organization> allOrganizations = organizationService.getAllOrganizations();
        model.addAttribute("allOrganizations", allOrganizations);
        return "superadmin/all-organizations";
    }

    @PostMapping("/delete-organization/{orgId}")
    String deleteOrganization(@PathVariable Long orgId, RedirectAttributes redirectAttributes) {
        if (organizationService.findById(orgId).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Organization not found");
            return "redirect:/api/superadmin/all-organizations";
        }
        organizationService.deleteOrganization(orgId);
        redirectAttributes.addFlashAttribute("success", "Organization deleted successfully");
        return "redirect:/api/superadmin/all-organizations";
    }

    @GetMapping("/login")
    String loginPage() {
        return "superadmin/login";
    }

    @GetMapping("/register")
    String registerPage() {
        return "superadmin/register";
    }

    @PostMapping("/register")
    String registerPage(@Valid @RequestBody SuperAdmin user) {
        superAdminService.save(user);
        return "superadmin/register";
    }
}


