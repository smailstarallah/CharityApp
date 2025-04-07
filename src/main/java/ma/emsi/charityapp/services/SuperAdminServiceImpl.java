package ma.emsi.charityapp.services;

import jakarta.transaction.Transactional;
import ma.emsi.charityapp.Enum.OrganizationStatus;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.entities.SuperAdmin;
import ma.emsi.charityapp.repositories.SuperAdminRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    SuperAdminRepository superAdminRepository;

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
    public void approveOrganization(Organization organization) {
        if (organization == null) {
            throw new IllegalArgumentException("L'organisation est nulle.");
        }
        organization.setStatus(OrganizationStatus.Approved);
    }
}
