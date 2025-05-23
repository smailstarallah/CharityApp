package ma.emsi.charityapp.validation;

import ma.emsi.charityapp.repositories.CharityActionRepository;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import ma.emsi.charityapp.repositories.SuperAdminRepository;
import ma.emsi.charityapp.services.RegularUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UniqueCheckService {

    private static final Logger logger = LoggerFactory.getLogger(UniqueCheckService.class);

    private final RegularUserRepository regularUserRepository;
    private final SuperAdminRepository superAdminRepository;
    private final CharityActionRepository charityActionRepository;

    public UniqueCheckService(RegularUserRepository regularUserRepository, SuperAdminRepository superAdminRepository, CharityActionRepository charityActionRepository) {
        this.regularUserRepository = regularUserRepository;
        this.superAdminRepository = superAdminRepository;
        this.charityActionRepository = charityActionRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public boolean existsByField(String fieldName, String parameterName, Object value) {
        if (value == null) {
            logger.warn("Value is null for {} = {}", parameterName, value);
            return false;
        }

        try {
            logger.debug("Checking existence for field {} with value {}", fieldName, value);
            boolean exists;
            if ("email".equals(fieldName)) {
                exists = regularUserRepository.existsByEmail((String) value) ||
                        superAdminRepository.existsByEmail((String) value);
            } else if ("telephone".equals(fieldName)) {
                exists = regularUserRepository.existsByTelephone((String) value) ||
                        superAdminRepository.existsByTelephone((String) value);
            } else if ("titre".equals(fieldName)){
                exists = charityActionRepository.existsByTitre((String) value);
            } else {
                logger.warn("Unknown field: {}", fieldName);
                return false;
            }

            logger.debug("Existence check for {} '{}': exists = {}", fieldName, value, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking existence for {} '{}': {}", fieldName, value, e.getMessage(), e);
            throw e;
        }
    }
}