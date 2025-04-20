package ma.emsi.charityapp.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.emsi.charityapp.Enum.OrganizationStatus;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.repositories.SuperAdminRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

    SuperAdminRepository superAdminRepository;
    OrganizationService organizationService;

    public SuperAdminServiceImpl(SuperAdminRepository superAdminRepository, OrganizationService organizationService) {
        this.superAdminRepository = superAdminRepository;
        this.organizationService = organizationService;
    }

    @Override
    public SuperAdmin save(SuperAdmin superAdmin) {
    if (superAdminRepository.findByEmail(superAdmin.getEmail()) != null) {
        throw new IllegalArgumentException("L'email existe déjà.");
    }
        return superAdminRepository.save(superAdmin);
    }

    @Override
    public SuperAdmin findById(Long id) {
        SuperAdmin superAdmin = superAdminRepository.findById(id).orElse(null);
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin non trouvé.");
        }
        return superAdmin;
    }

    @Override
    public void deleteById(Long id) {
        SuperAdmin superAdmin = superAdminRepository.findById(id).orElse(null);
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin non trouvé.");
        }
        superAdminRepository.deleteById(id);
    }

    @Override
    public SuperAdmin update(Long id, SuperAdmin superAdmin) {
        SuperAdmin existingUser = superAdminRepository.findById(id).orElse(null);
        if (superAdmin.getNom().isEmpty() || superAdmin.getPreNom().isEmpty() || superAdmin.getTelephone().isBlank() ) {
            throw new IllegalArgumentException("un champs est vide.");
        }
        BeanUtils.copyProperties(superAdmin, existingUser, "Id", "email", "password", "dateNaissance");
        return existingUser;
    }

    @Override
    public SuperAdmin findByEmail(String email) {
        SuperAdmin superAdmin = superAdminRepository.findByEmail(email);
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin non trouvé.");
        }
        return superAdmin;
    }

    @Override
    public List<SuperAdmin> getAll() {
        List<SuperAdmin> superAdmins = superAdminRepository.findAll();
        if (superAdmins != null && !superAdmins.isEmpty()) {
            return superAdmins;
        }
        throw new IllegalArgumentException("Aucun SuperAdmin trouvé.");
    }

    @Override
    @Transactional
    public void approveOrganization( Long id, Long orgId) {
        if (id == null || orgId == null || id <= 0 || orgId <= 0) {
            throw new IllegalArgumentException("ID ne doit pas être null.");
        }
        Organization organization = organizationService.findById(orgId).orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        SuperAdmin superAdmin = superAdminRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("SuperAdmin not found"));
        organization.setStatus(OrganizationStatus.Approved);
        organization.setSuperAdmin(superAdmin);
    }
}
