package ma.emsi.charityapp.services;

import ma.emsi.charityapp.dto.ViewCharityDTO;
import ma.emsi.charityapp.entities.CharityAction;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CharityActionService {
    CharityAction save(CharityAction charityAction);
    CharityAction findById(Long id);
    void deleteById(Long id);
    void updateFondActuelById(double fondsActuels, Long Id);
    List<CharityAction> findCharityActionByUserAndOrganization(Long idUser, Long idOrg);
//    CharityAction findByTitre(String titre);
    List<ViewCharityDTO> getAll();
//    List<CharityAction> getAllByOrganizationId(Long orgId);
    boolean existsById(Long id);
    void updateCharityAction(Long id, CharityAction charityAction, MultipartFile[] mediaFiles);
}