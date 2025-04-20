package ma.emsi.charityapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.entities.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UniqueValidator implements ConstraintValidator<Unique, Object> {

    private static final Logger logger = LoggerFactory.getLogger(UniqueValidator.class);

    private final UniqueCheckService uniqueCheckService;

    private String fieldName;
    private Class<?> entity;
    private String message;

    public UniqueValidator(UniqueCheckService uniqueCheckService) {
        this.uniqueCheckService = uniqueCheckService;
    }

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.entity = constraintAnnotation.entity();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        logger.debug("Validating {} for value: {}", fieldName, value);

        try {
            if (entity.equals(Users.class) || entity.equals(CharityAction.class)) {
                return checkUserUniqueness(value, context);
            }
            return true;
        } catch (Exception e) {
            logger.error("Error validating {}: {}", fieldName, e.getMessage(), e);
            return false;
        }
    }

    private boolean checkUserUniqueness(Object value, ConstraintValidatorContext context) {
        boolean exists = uniqueCheckService.existsByField(fieldName, "value", value);
        boolean isUnique = !exists;

        logger.debug("Checked uniqueness for {} '{}': exists = {}, isUnique = {}", fieldName, value, exists, isUnique);

        if (!isUnique) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }

        return isUnique;
    }
}