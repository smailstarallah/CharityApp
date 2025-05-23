package ma.emsi.charityapp.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.charityapp.dto.ViewCharityDTO;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.repositories.CharityActionRepository;
import ma.emsi.charityapp.repositories.MediaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CharityActionServiceImpl implements CharityActionService {

    final private CharityActionRepository charityActionRepository;
    private final MediaService mediaService;
    @PersistenceContext
    private EntityManager entityManager;

    public CharityActionServiceImpl(CharityActionRepository charityActionRepository, MediaService mediaService) {
        this.charityActionRepository = charityActionRepository;
        this.mediaService = mediaService;
    }

    @Override
    public CharityAction save(CharityAction charityAction) {
        if(charityAction.getFondsActuels() == null) {
            charityAction.setFondsActuels(0.0);
        }
        return charityActionRepository.save(charityAction);
    }

    @Override
    public CharityAction findById(Long id) {
        CharityAction charityAction = charityActionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Charity Action not found"));
        charityAction.setMedia(mediaService.findByCharityActionId(id));
        return charityAction;

    }

    @Override
    public void deleteById(Long id) {
        if (charityActionRepository.existsById(id)) {
            charityActionRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("CharityAction not found");
        }
    }


    @Override
    public void updateFondActuelById(double fondsActuels, Long Id) {
        if (charityActionRepository.existsById(Id)) {
            log.info("Updating fond actuel for CharityAction with id {}", Id);
            charityActionRepository.updateFondActuelById(fondsActuels, Id);
        } else {
            throw new EntityNotFoundException("CharityAction not found");
        }
    }

    @Override
    public List<CharityAction> findCharityActionByUserAndOrganization(Long idUser, Long idOrg) {
        List<CharityAction> charityActions = charityActionRepository.findCharityActionByUserAndOrganization(idUser, idOrg);

        charityActions.stream().forEach(a -> a.setMedia(mediaService.findByCharityActionId(a.getId())));
        return charityActions;
    }

//    @Override
//    public CharityAction findByTitre(String titre) {
//        return charityActionRepository.findByTitre(titre);
//    }

    @Override
    public List<ViewCharityDTO> getAll() {
        List<CharityAction> charityActions = charityActionRepository.findAll();
        charityActions.stream().forEach(a -> a.setMedia(mediaService.findByCharityActionId(a.getId())));
        List<ViewCharityDTO> viewCharityDTOS = new ArrayList<>();
        charityActions.stream().forEach(a -> {viewCharityDTOS.add(new ViewCharityDTO(a));});
        return viewCharityDTOS;
    }

    @Override
    public boolean existsById(Long id) {
        return charityActionRepository.existsById(id);
    }

    @Override
    @Transactional
    public void updateCharityAction(Long id, CharityAction charityAction, MultipartFile[] mediaFiles) {
        try {
            charityActionRepository.updateCharityAction(id, charityAction);
            log.info("CharityAction with id {} updated successfully", id);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            if(mediaFiles != null) {
                log.info("Saving media files for CharityAction with id {}", id);
                mediaService.saveMedia(mediaFiles, charityAction);
                log.info("Media files saved successfully for CharityAction with id {}", id);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

//    @Override
//    public List<CharityAction> getAllByOrganizationId(Long orgId) {
//        return charityActionRepository.findAllByOrganization(orgId);
//    }
}
