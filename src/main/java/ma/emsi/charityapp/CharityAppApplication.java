package ma.emsi.charityapp;

import ma.emsi.charityapp.Enum.UserType;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import ma.emsi.charityapp.repositories.SuperAdminRepository;
import ma.emsi.charityapp.services.RegularUserServiceImpl;
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

    public static void main(String[] args) {
        SpringApplication.run(CharityAppApplication.class, args);
    }

    public void run(String... args) throws Exception {
        regularUserRepository.save(new RegularUser("test", "test", "tesat@gmail.ma", 123456789, new java.util.Date(), "test", UserType.REGULAR_USER));
        regularUserRepository.save(new RegularUser("test", "test", "taest@gmail.ma", 12346789, new java.util.Date(), "test", UserType.REGULAR_USER));
        regularUserRepository.save(new RegularUser("test", "test", "testa@gmail.ma", 1234789, new java.util.Date(), "test", UserType.REGULAR_USER));
        regularUserRepository.save(new RegularUser("test", "test", "ateastatest@gmail.ma", 12456789, new java.util.Date(), "test", UserType.REGULAR_USER));
        superAdminRepository.save(new SuperAdmin("test", "test", "ateastatest@gmail.ma", 12456789, new java.util.Date(), "test"));
        superAdminRepository.save(new SuperAdmin("test", "test", "ateast@gmail.ma", 1245789, new java.util.Date(), "test"));
    }
}
