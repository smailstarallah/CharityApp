package ma.emsi.charityapp.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.services.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {
    @Autowired
    SuperAdminService superAdminService;

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

    @PostMapping("/save")
    SuperAdmin save(@Valid @RequestBody SuperAdmin superAdmin) {
//        int random = (int) (Math.random() * 10000000);
//        SuperAdmin superAdmin = new SuperAdmin("a", "a", "aaaa"+ (char)(random % 100) + ( (char) (int) (Math.random() * 100))  , random, new java.util.Date(1999, 4, 12), "a");
        return superAdminService.save(superAdmin);
    }

    @PutMapping("/update/{id}")
    SuperAdmin update(@PathVariable Long id,@Valid @RequestBody SuperAdmin superAdmin) {
        return superAdminService.update(id, superAdmin);
    }
}
