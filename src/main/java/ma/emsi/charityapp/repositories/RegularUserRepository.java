package ma.emsi.charityapp.repositories;


import jakarta.transaction.Transactional;
import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.entities.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Transactional
public interface RegularUserRepository extends JpaRepository<RegularUser,Long> {
    RegularUser findByEmail(String email);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
    @Query("SELECT sa FROM SuperAdmin sa LEFT JOIN FETCH sa.organizations WHERE sa.Id = :id")
    List<Donation> findOrganizationByIdsa(Long id);
    /*
    * @Query("SELECT sa FROM SuperAdmin sa LEFT JOIN FETCH sa.organizations WHERE sa.id = :id")
    Optional<SuperAdmin> findSuperAdminWithOrganizationsById(Long id);
    * */
}
