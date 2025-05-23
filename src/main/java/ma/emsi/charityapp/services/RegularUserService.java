package ma.emsi.charityapp.services;

import ma.emsi.charityapp.dto.OrganizationRegisterDTO;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RegularUserService {
    RegularUser save(RegularUser regularUser);
    RegularUser findById(Long id);
    void deleteById(Long id);
    RegularUser update(Long id, RegularUser regularUser);
    RegularUser findByEmail(String email);
    List<RegularUser> getAll();
    List voirHistDesDons(Long id);
    Donation makeDonation(Donation d);
    void registerOrganization(Long id, OrganizationRegisterDTO org, MultipartFile file) throws IOException;
    public Long registerCharityAction(Long userId, Long orgId, CharityAction charityAction, MultipartFile[] mediaFiles) throws IOException;
}
