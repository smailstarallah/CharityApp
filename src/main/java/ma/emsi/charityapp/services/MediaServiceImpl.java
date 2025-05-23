package ma.emsi.charityapp.services;

import ma.emsi.charityapp.Enum.MediaType;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.Media;
import ma.emsi.charityapp.repositories.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
public class MediaServiceImpl implements MediaService {

    MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }


    @Override
    public void saveMedia(MultipartFile[] mediaFiles, CharityAction charityAction) throws IOException {
        for (int i = 0; i < mediaFiles.length; i++) {
            Media media = new Media();
            media.setCharityAction(charityAction);
            media.setType(mediaFiles[i].getContentType().startsWith("image") ? MediaType.Image : MediaType.Video);
            media.setContent(mediaFiles[i].getBytes());
            media.setFileName(mediaFiles[i].getOriginalFilename());
            media.setContentType(mediaFiles[i].getContentType());
            media.setFileSize(mediaFiles[i].getSize());
            mediaRepository.save(media);
        }
    }

    @Override
    public Set<Media> findByCharityActionId(Long charityActionId) {
        return mediaRepository.findByCharityActionId(charityActionId);
    }

    @Override
    public void deleteMediaById(Long id) {
        mediaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return mediaRepository.existsById(id);
    }
}
