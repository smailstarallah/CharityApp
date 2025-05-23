package ma.emsi.charityapp.controllers;


import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.charityapp.dto.CreateDonationRequest;
import ma.emsi.charityapp.dto.ExecutePaymentRequest;
import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.services.CharityActionService;
import ma.emsi.charityapp.services.DonationService;
import ma.emsi.charityapp.services.PayPalService;
import ma.emsi.charityapp.services.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/donations")
public class DonationController {

    @Autowired
    private PayPalService payPalService;
    @Autowired
    RegularUserController regularUserController;
    @Autowired
    DonationService donationService;
    @Autowired
    private RegularUserService regularUserService;
    @Autowired
    private CharityActionService charityActionService;

    public DonationController (DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping("/add")
    public String addDonation(Donation donation) {
        donationService.addDonation(donation);
        return "Donation added successfully!";
    }

    @GetMapping("/All")
    public List<Donation> getAllDonations() {
        return donationService.getAllDonations();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDonation(@RequestBody CreateDonationRequest request) {
        log.info("Creating donation with amount: {}", request.getAmount());
        log.info("Creating donation with currency: {}", request.getCurrency());
        log.info("Creating donation with charityId: {}", request.getCharityId());
        log.info("Creating donation with userId: {}", request.getUserId());

        try {
            Payment payment = payPalService.createPayment(request);

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(Map.of(
                            "redirectUrl", links.getHref()
                    ));
                }
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Erreur lors de la création du paiement"
            ));

        } catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/execute")
    @Transactional
    public ResponseEntity<?> executePayment(@RequestBody ExecutePaymentRequest request) {
        log.info("Executing payment with ID: {}", request.getPaymentId());
        try {
            Payment payment = payPalService.executePayment(request.getPaymentId(), request.getPayerId());

            if ("approved".equals(payment.getState())) {
                log.info("Payment approved: {}", payment.getId());
                // Sauvegarder le don en base après paiement réussi
                System.out.println("==>" + request.getUserId() + " || " + request.getCharityId());
                Donation donation = new Donation(request.getAmount(), regularUserService.findById(request.getUserId()), charityActionService.findById(602L));
                regularUserController.makeDonation(donation, request.getUserId(), 602L);
                log.info("Donation saved successfully");
                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Paiement échoué"));
            }

        } catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
