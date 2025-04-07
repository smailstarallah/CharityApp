package ma.emsi.charityapp.repositories;


import jakarta.transaction.Transactional;
import ma.emsi.charityapp.entities.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Transactional
public interface RegularUserRepository extends JpaRepository<RegularUser,Long> {
    RegularUser findByEmail(String email);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
}
