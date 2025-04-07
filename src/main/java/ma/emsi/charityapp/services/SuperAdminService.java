package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.entities.SuperAdmin;
import java.util.List;

public interface SuperAdminService {
    SuperAdmin save(SuperAdmin superAdmin);
    SuperAdmin findById(Long id);
    void deleteById(Long id);
    SuperAdmin update(Long id, SuperAdmin superAdmin);
    SuperAdmin findByEmail(String email);
    List<SuperAdmin> getAll();
    void approveOrganization(Organization organization);
}
