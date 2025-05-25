package ma.emsi.charityapp.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.emsi.charityapp.Enum.OrganizationStatus;
import ma.emsi.charityapp.dto.OrganizationRegisterDTO;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.repositories.OrganizationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);
    OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Enregistre une nouvelle organisation avec statut initial "En attente"
     * @param o Les données de l'organisation à enregistrer
     * @return DTO de l'organisation enregistrée avec son ID généré
     * @throws IllegalArgumentException Si les données d'entrée sont invalides
     */
    @Override
    @Transactional
    public OrganizationRegisterDTO registreOrganization(OrganizationRegisterDTO o, MultipartFile file) throws IOException {
        logger.info("Tentative d'enregistrement d'une nouvelle organisation: {}", o.getNom());
        
        // Vérification si l'identifiant fiscal existe déjà
        if (organizationRepository.existsByIdentifiantFiscal(o.getIdentifiantFiscal())) {
            logger.error("Identifiant fiscal déjà utilisé: {}", o.getIdentifiantFiscal());
            throw new IllegalArgumentException("Une organisation avec cet identifiant fiscal existe déjà");
        }
        
        Organization organization = new Organization();
        organization.setNom(o.getNom());
        organization.setIdentifiantFiscal(o.getIdentifiantFiscal());
        organization.setContactPrincipal(o.getContactPrincipal());
        organization.setStatus(OrganizationStatus.Pending);
        organization.setAdresseLegale(o.getAdresseLegale());
        organization.setMissionDescription(o.getMissionDescription());
        organization.setRUser(o.getRUser());
        organization.setLogo(file.getBytes());
        try {
            organization = organizationRepository.save(organization);
            logger.info("Organisation enregistrée avec succès avec ID: {}", organization.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'organisation", e);
            throw new RuntimeException("Échec de l'enregistrement de l'organisation", e);
        }
        
        o.setId(organization.getId());
        return o;
    }

    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return organizationRepository.findById(id);
    }

    @Override
    public Organization getOrganizationById(Long id) {
        return organizationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Organisation avec l'ID " + id + " non trouvée"));
    }

    @Override
    @Transactional
    public void updateOrganization(Long id, Organization o, MultipartFile file) throws IOException {
        Organization existingOrganization = organizationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Organisation avec l'ID " + id + " non trouvée"));
        if (file != null && !file.isEmpty())
            existingOrganization.setLogo(file.getBytes());
        if(o.getNom() != null)
            existingOrganization.setNom(o.getNom());
        if(o.getContactPrincipal() != null)
            existingOrganization.setContactPrincipal(o.getContactPrincipal());
        if(o.getAdresseLegale() != null)
            existingOrganization.setAdresseLegale(o.getAdresseLegale());
        if(o.getMissionDescription() != null)
            existingOrganization.setMissionDescription(o.getMissionDescription());
        organizationRepository.save(existingOrganization);
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        if (organizationRepository.existsById(id)) {
            organizationRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Organisation avec l'ID " + id + " non trouvée");
        }
    }

    @Override
    public boolean existsByIdentifiantFiscal(String identifiantFiscal) {
        Organization organization = organizationRepository.findByIdentifiantFiscal(identifiantFiscal);
        return organization != null;
    }

    @Override
    public boolean existsByContactPrincipal(String contactPrincipal) {
        Organization organization = organizationRepository.findByContactPrincipal(contactPrincipal);
        return organization != null;
    }

    @Override
    public List<Organization> findOrganizationByUser(RegularUser User) {
        return organizationRepository.findByRUser(User);
    }


    @Override
    @Transactional
    public Organization addMissionDescription(Long id, String missionDescription) {
        if(id == null){
            return null;
        }
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Organisation avec l'ID " + id + " non trouvée"));
        organization.setMissionDescription(missionDescription);
        return organizationRepository.save(organization);
    }

    @Override
    public List<Organization> findAllByStatus(OrganizationStatus status) {
        return organizationRepository.findAllByStatus(status);
    }
}
