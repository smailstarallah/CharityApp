package ma.emsi.charityapp.repositories;

import jakarta.transaction.Transactional;
import ma.emsi.charityapp.entities.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
@Transactional
public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    SuperAdmin findByEmail(String email);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
}
