package ma.emsi.charityapp.repositories;


import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import ma.emsi.charityapp.Enum.UserType;
import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.entities.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface RegularUserRepository extends JpaRepository<RegularUser,Long> {
    RegularUser findByEmail(String email);
    void deleteById(@NotNull Long id);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
    @Query("SELECT d FROM Donation d WHERE d.rUser.Id = :id")
    List<Donation> findOrganizationByIdru(@Param("id") Long id);
    @Modifying
    @Query("UPDATE RegularUser u SET u.type = :type WHERE u.Id = :id")
    void updateType(@Param("id") Long id, @Param("type") UserType type);
    @Modifying
    @Transactional
    @Query("UPDATE RegularUser u SET u.charityAction.Id = :charityActionId WHERE u.Id = :id")
    void updateCharityAction(@Param("id") Long id, @Param("charityActionId") Long charityActionId);

    /*
    * @Query("SELECT sa FROM SuperAdmin sa LEFT JOIN FETCH sa.organizations WHERE sa.id = :id")
    Optional<SuperAdmin> findSuperAdminWithOrganizationsById(Long id);
    * */
}
