package ma.emsi.charityapp;

import ma.emsi.charityapp.Enum.OrganizationStatus;
import ma.emsi.charityapp.Enum.UserType;
import ma.emsi.charityapp.entities.*;
import ma.emsi.charityapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CharityAppApplication implements CommandLineRunner {
    @Autowired
    RegularUserRepository regularUserRepository;
    @Autowired
    SuperAdminRepository superAdminRepository;
    @Autowired
    DonationRepository donationRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    CharityActionRepository charityActionRepository;

    public static void main(String[] args) {
        SpringApplication.run(CharityAppApplication.class, args);
    }

    public void run(String... args) {
            regularUserRepository.save(new RegularUser("test", "test", "tesat@gmail.ma", "0634567891", new java.util.Date(), "test", UserType.REGULAR_USER));
            regularUserRepository.save(new RegularUser("test", "test", "yui@gmail.ma", "0600000098", new java.util.Date(), "test", UserType.REGULAR_USER));
            regularUserRepository.save(new RegularUser("test", "test", "taest@gmail.ma", "0623478987", new java.util.Date(), "test", UserType.REGULAR_USER));
            regularUserRepository.save(new RegularUser("test", "test", "ateastatest@gmail.ma", "0612456789", new java.util.Date(), "test", UserType.REGULAR_USER));
            superAdminRepository.save(new SuperAdmin("test", "test", "azer@gmail.ma", "0624567891", new java.util.Date(), "test"));
            superAdminRepository.save(new SuperAdmin("test", "test", "aze@gmail.ma", "0624578912", new java.util.Date(), "test"));

        donationRepository.save(new Donation(null, 200.0, new java.util.Date(), regularUserRepository.findById(1L).get(), null));
        donationRepository.save(new Donation(null, 200.0, new java.util.Date(), regularUserRepository.findById(1L).get(), null));
        donationRepository.save(new Donation(null, 200.0, new java.util.Date(), regularUserRepository.findById(1L).get(), null));
        donationRepository.save(new Donation(null, 200.0, new java.util.Date(), regularUserRepository.findById(1L).get(), null));

        organizationRepository.save(new Organization(null, "test", "test","test", "test","test","test", OrganizationStatus.Pending, regularUserRepository.findById(2L).get(), null, null));
        organizationRepository.save(new Organization(null, "test", "test","test", "test","test","test", OrganizationStatus.Pending, null, null, null));
        organizationRepository.save(new Organization(null, "test", "test","test", "test","test","test", OrganizationStatus.Pending, null, null, null));

//        charityActionRepository.save(new CharityAction(null, "test", "test", null, "test", 200.0, 0.0, "test", null, null, null, organizationRepository.findById(1L).get()));
        CharityAction action = new CharityAction();
        action.setTitre("Aide aux sans-abris");
        action.setDescription("Collecte de fonds pour les sans-abris");
        action.setLocalisation("Lyon");
        action.setObjectifDeFinancement(5000.0);
        action.setFondsActuels(0.0);
        action.setCategorie("Social");
        action.setOrganization(organizationRepository.findById(1L).get());

        charityActionRepository.save(action);
    }
}
