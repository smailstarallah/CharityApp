package ma.emsi.charityapp.repositories;

import ma.emsi.charityapp.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Set<Media> findByCharityActionId(Long charityActionId);
}
