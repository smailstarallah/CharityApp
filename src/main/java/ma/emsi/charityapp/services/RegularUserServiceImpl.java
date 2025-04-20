package ma.emsi.charityapp.services;

import jakarta.persistence.EntityNotFoundException;
import ma.emsi.charityapp.Enum.UserType;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.repositories.OrganizationRepository;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RegularUserServiceImpl implements RegularUserService {

    private static final Logger logger = LoggerFactory.getLogger(RegularUserService.class);

    private final OrganizationRepository organizationRepository;
    RegularUserRepository regularUserRepository;
    DonationService donationService;
    CharityActionService charityActionService;

    public RegularUserServiceImpl(RegularUserRepository regularUserRepository, DonationService donationService,
                                  OrganizationRepository organizationRepository, CharityActionService charityActionService) {
        this.regularUserRepository = regularUserRepository;
        this.donationService = donationService;
        this.organizationRepository = organizationRepository;
        this.charityActionService = charityActionService;
    }

    @Override
    public RegularUser save( RegularUser regularUser) {
//        if (regularUserRepository.findByEmail(regularUser.getEmail()) != null) {
//            throw new IllegalArgumentException("L'email existe déjà.");
//        }
        regularUserRepository.save(regularUser);
        return regularUser;
    }

    @Override
    public RegularUser findById(Long id) {
         return regularUserRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("Utilisateur non trouvé"));
    }

    @Override
    public void deleteById(Long id) {
        if (regularUserRepository.existsById(id)) {
            regularUserRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Utilisateur non trouvé");
        }
    }

    @Override
    public RegularUser update(Long id, RegularUser regularUser) {
        RegularUser existingUser = regularUserRepository.findById(id).get();
        BeanUtils.copyProperties(regularUser, existingUser, "Id", "email", "password", "dateNaissance");
        return existingUser;
    }

    @Override
    public RegularUser findByEmail(String email) {
        RegularUser regularUser = regularUserRepository.findByEmail(email);
        if (regularUser != null) {
            return regularUser;
        } else {
            throw new EntityNotFoundException("Utilisateur non trouvé");
        }
    }


    @Override
    public List<RegularUser> getAll() {
        return regularUserRepository.findAll();
    }

    @Override
    public List<Donation> voirHistDesDons(Long id) {
        return regularUserRepository.findOrganizationByIdru(id);
    }

    @Override
    public void makeDonation(Donation D) {
        donationService.addDonation(D);
    }

    @Override
    public void registerOrganization(Long id, Organization org) {
        if (regularUserRepository.findById(id).get().getType() == UserType.REGULAR_USER) {
            regularUserRepository.updateType(id, UserType.ADMIN);
        }
        org.setRUser(regularUserRepository.findById(id).get());
        organizationRepository.save(org);
    }

    @Override
    public Long registerCharityAction(Long userId, Long orgId, CharityAction charityAction) {
        logger.debug("Enregistrement d'une action caritative pour l'utilisateur ID={} et l'organisation ID={}", userId, orgId);
        if (userId == null || orgId == null || charityAction == null) {
            logger.error("Utilisateur, organisation ou action null");
            throw new IllegalArgumentException("L'utilisateur, l'organisation ou l'action caritative ne peut pas être null");
        }

//        RegularUser user = regularUserRepository.findById(userId).orElseThrow(() -> {
//            logger.error("Organisation avec l'ID {} non trouvée", orgId);
//            return new EntityNotFoundException("Utilisateur non trouvé");
//        });
        Organization organization = organizationRepository.findById(orgId).orElseThrow(() -> {
            logger.error("Organisation avec l'ID {} non trouvée", orgId);
            return new EntityNotFoundException("Organisation non trouvée");
        });
        charityAction.setOrganization(organization);
        logger.debug("Avant sauvegarde de l'action caritative: {}", charityAction);
        CharityAction savedAction;
        try {
            savedAction = charityActionService.save(charityAction);
            logger.debug("Action caritative sauvegardée avec ID={}", savedAction.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de l'action caritative: {}", e.getMessage(), e);
            throw new IllegalStateException("Impossible de sauvegarder l'action caritative: " + e.getMessage(), e);
        }
//        user.setCharityAction(savedAction);
        System.out.println(savedAction);
        if (charityActionService.existsById(charityAction.getId())) {
            logger.info("Action caritative enregistrée avec succès");
            regularUserRepository.updateCharityAction(userId, charityAction.getId());
        } else {
            logger.error("Erreur lors de l'enregistrement de l'action caritative");
        }
        logger.debug("Utilisateur avant sauvegarde: id={}, charityActionId={}", userId, savedAction.getId());
        return charityAction.getId();
//        try {
//            RegularUser updatedUser = regularUserRepository.save(user);
//            logger.info("Action caritative enregistrée pour l'utilisateur ID={}", userId);
//            return updatedUser;
//        } catch (DataIntegrityViolationException e) {
//            logger.error("Erreur de contrainte lors de l'enregistrement de l'action caritative pour l'utilisateur ID={}: {}", userId, e.getMessage());
//            throw new IllegalStateException("Impossible d'enregistrer l'action caritative en raison d'une violation de contrainte", e);
//        }catch (Exception e) {
//            logger.error("Erreur inattendue lors de l'enregistrement de l'utilisateur ID={}: {}", userId, e.getMessage(), e);
//            throw new IllegalStateException("Erreur inattendue lors de l'enregistrement de l'action caritative: " + e.getMessage(), e);
//        }
    }


}
