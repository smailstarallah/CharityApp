package ma.emsi.charityapp.services;


import ma.emsi.charityapp.dto.OrganizationRegisterDTO;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    OrganizationRegisterDTO registreOrganization(OrganizationRegisterDTO o, MultipartFile file) throws IOException;
    List<Organization> getAllOrganizations();
    Optional<Organization> findById(Long id);
    Organization getOrganizationById(Long id);
    void updateOrganization(Long id, Organization o, MultipartFile file) throws IOException;
    void deleteOrganization(Long id);
    boolean existsByIdentifiantFiscal(String identifiantFiscal);
    boolean existsByContactPrincipal(String contactPrincipal);
    List<Organization> findOrganizationByUser(RegularUser User);
    Organization addMissionDescription(Long id, String missionDescription);
}
