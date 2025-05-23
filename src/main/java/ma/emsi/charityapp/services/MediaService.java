package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.Media;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

public interface MediaService {
    void saveMedia(MultipartFile[] file, CharityAction charityAction) throws IOException;
    Set<Media> findByCharityActionId(Long charityActionId);
    void deleteMediaById(Long id);
    public boolean existsById(Long id);
}
