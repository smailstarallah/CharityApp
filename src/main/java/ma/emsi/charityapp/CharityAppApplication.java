package ma.emsi.charityapp;

import ma.emsi.charityapp.Enum.UserType;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import ma.emsi.charityapp.repositories.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class CharityAppApplication implements CommandLineRunner {
    @Autowired
    RegularUserRepository regularUserRepository;
    @Autowired
    SuperAdminRepository superAdminRepository;

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
    }
}
