package ma.emsi.charityapp.repositories;

import jakarta.transaction.Transactional;
import ma.emsi.charityapp.entities.CharityAction;
import ma.emsi.charityapp.entities.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CharityActionRepository extends JpaRepository<CharityAction, Long> {
     CharityAction save(CharityAction charityAction);
     boolean existsByTitre(String titre);
     @Modifying
     @Query("update CharityAction c set c.fondsActuels = ?1 where c.Id = ?2")
     void updateFondActuelById(double fondsActuels, Long Id);
     @Query("select c from CharityAction c where c.organization.id = ?2 and c.organization.rUser.Id = ?1")
     List<CharityAction> findCharityActionByUserAndOrganization(Long idUser, Long idOrg);

     @Modifying
     @Transactional
     @Query("UPDATE CharityAction c SET c.titre = :#{#charity.titre}, c.description = :#{#charity.description}, " +
             "c.localisation = :#{#charity.localisation}, c.dateDebut = :#{#charity.dateDebut}, " +
             "c.objectifDeFinancement = :#{#charity.objectifDeFinancement}, c.categorie = :#{#charity.categorie} " +
             "WHERE c.id = :id")
     void updateCharityAction(@Param("id") Long id, @Param("charity") CharityAction charityAction);
}