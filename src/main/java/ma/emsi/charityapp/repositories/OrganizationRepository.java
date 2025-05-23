package ma.emsi.charityapp.repositories;

import jakarta.transaction.Transactional;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Transactional
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Organization save(Organization organization);
    Organization findByNom(String nom);
    Organization findByIdentifiantFiscal(String identifiantFiscal);
    boolean existsByIdentifiantFiscal(String identifiantFiscal);
    Organization findByContactPrincipal(String contactPrincipal);
    @Query("SELECT o FROM Organization o WHERE o.rUser = ?1")
    List<Organization> findByRUser(RegularUser rUser);
    Organization findByAdresseLegale(String adresseLegale);
}
