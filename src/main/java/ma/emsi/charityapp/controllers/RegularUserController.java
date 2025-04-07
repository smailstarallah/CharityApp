package ma.emsi.charityapp.controllers;

import jakarta.validation.Valid;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.services.RegularUserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/regularusers")
//@CrossOrigin(origins = "http://localhost:3000") // Allow requests from React app
public class RegularUserController {
    RegularUserService regularUserService;

    public RegularUserController(RegularUserService regularUserService) {
        this.regularUserService = regularUserService;
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
            e.printStackTrace();
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

}
