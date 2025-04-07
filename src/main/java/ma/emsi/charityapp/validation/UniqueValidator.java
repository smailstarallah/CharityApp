package ma.emsi.charityapp.validation;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintValidator;
import ma.emsi.charityapp.entities.Users;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import ma.emsi.charityapp.repositories.SuperAdminRepository;

import org.springframework.stereotype.Component;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

@Component
public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    //@Autowired
    private RegularUserRepository regularUserRepos; // You’ll need to inject the repository
    //@Autowired
    private SuperAdminRepository superAdminRepository; // You’ll need to inject the repository

    private String fieldName;
    private Class<?> entity;

    public UniqueValidator(RegularUserRepository regularUserRepos, SuperAdminRepository superAdminRepository) {
        this.regularUserRepos = regularUserRepos;
        this.superAdminRepository = superAdminRepository;
        System.out.println("-------- constructor INJECTED");
    }

/*    public UniqueValidator() {
        System.out.println("-------- constructor NO ARGS");
    }*/

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.entity = constraintAnnotation.entity();
        System.out.println("-------- initialize");
    }

    @Override
    @Transactional
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null checks
        }

        // Check if the value already exists in the database
        if (entity.equals(Users.class) && fieldName.equals("email")) {
            System.out.println("-------- email");
            return !regularUserRepos.existsByEmail((String) value) ||
                    !superAdminRepository.existsByEmail((String) value);
        }

        if (entity.equals(Users.class) && fieldName.equals("telephone")) {
            System.out.println("-------- telephone");
            return !regularUserRepos.existsByTelephone((String) value) ||
                    !superAdminRepository.existsByTelephone((String) value);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("------------------------------------------------------");
        System.out.println("Entity: " + entity.getSimpleName() + ", Field: " + fieldName + ", Value: " + value);
        System.out.println(context.getDefaultConstraintMessageTemplate());
        // Add more conditions for other entities/fields if needed

        return true;
    }
}
