package ma.emsi.charityapp.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.emsi.charityapp.Enum.UserType;
import ma.emsi.charityapp.dto.OrganizationRegisterDTO;
import ma.emsi.charityapp.entities.*;
import ma.emsi.charityapp.repositories.MediaRepository;
import ma.emsi.charityapp.Enum.MediaType;
import ma.emsi.charityapp.repositories.OrganizationRepository;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class RegularUserServiceImpl implements RegularUserService {

    private static final Logger logger = LoggerFactory.getLogger(RegularUserService.class);

    private final RegularUserRepository regularUserRepository;
    private final DonationService donationService;
    private final CharityActionService charityActionService;
    private final OrganizationService organizationService;
    private final MediaService mediaService;

    public RegularUserServiceImpl(RegularUserRepository regularUserRepository, DonationService donationService,
                                  OrganizationRepository organizationRepository, CharityActionService charityActionService, OrganizationService organizationService,  MediaService mediaService) {
        this.regularUserRepository = regularUserRepository;
        this.donationService = donationService;
        this.charityActionService = charityActionService;
        this.organizationService = organizationService;
        this.mediaService = mediaService;
    }

    @Override
    public RegularUser save( RegularUser regularUser) {
        regularUser.setType(UserType.REGULAR_USER);
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
    @Transactional(rollbackOn = Exception.class)
    public Donation makeDonation(Donation donation) {

        CharityAction charityAction = donation.getCharityAction();
        //updateFondActuelById
        double newFondActuel = charityAction.getFondsActuels() + donation.getMontante();
        charityActionService.updateFondActuelById(newFondActuel, charityAction.getId());

        return donationService.addDonation(donation);
    }

    @Override
    public void registerOrganization(Long id, OrganizationRegisterDTO org, MultipartFile file) throws IOException {
        if (regularUserRepository.findById(id).get().getType() == UserType.REGULAR_USER) {
            regularUserRepository.updateType(id, UserType.ADMIN);
        }
        org.setRUser(regularUserRepository.findById(id).get());
        organizationService.registreOrganization(org, file);
    }

    @Override
    @Transactional
    public Long registerCharityAction(Long userId, Long orgId, CharityAction charityAction, MultipartFile[] mediaFiles) throws IOException {
        logger.debug("Enregistrement d'une action caritative pour l'utilisateur ID={} et l'organisation ID={}", userId, orgId);
        if (userId == null || orgId == null || charityAction == null) {
            logger.error("Utilisateur, organisation ou action null");
            throw new IllegalArgumentException("L'utilisateur, l'organisation ou l'action caritative ne peut pas être null");
        }

        Organization organization = organizationService.findById(orgId).orElseThrow(() -> {
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

        if (charityActionService.existsById(charityAction.getId())) {
            logger.info("Action caritative enregistrée avec succès");
            regularUserRepository.updateCharityAction(userId, charityAction.getId());
        } else {
            logger.error("Erreur lors de l'enregistrement de l'action caritative");
        }
        logger.debug("Utilisateur avant sauvegarde: id={}, charityActionId={}", userId, savedAction.getId());

        mediaService.saveMedia(mediaFiles, charityAction);

        regularUserRepository.updateCharityAction(userId, charityAction.getId());
        return charityAction.getId();
    }


}
