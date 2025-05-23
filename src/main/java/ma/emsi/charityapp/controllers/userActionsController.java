package ma.emsi.charityapp.controllers;


import ma.emsi.charityapp.services.UserActionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user-actions")
public class userActionsController {
    private final UserActionsService userActionsService;

    userActionsController(UserActionsService userActionsService) {
        this.userActionsService = userActionsService;
    }

    @PostMapping
    public ResponseEntity<String> logAction(@RequestBody Map<String, String> payload) {
        userActionsService.logAction(
                payload.get("userId"),
                payload.get("action"),
                payload.get("page"),
                payload.get("details")
        );
        return ResponseEntity.ok("Action logged successfully");
    }

    @PostMapping("/getall")
    public ResponseEntity<?> getAllActions() {
        return ResponseEntity.ok(userActionsService.getAllActions());
    }
}
