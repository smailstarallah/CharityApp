package ma.emsi.charityapp.services;

import jakarta.transaction.Transactional;
import ma.emsi.charityapp.entities.RegularUser;
import ma.emsi.charityapp.repositories.RegularUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class RegularUserServiceImpl implements RegularUserService {

    RegularUserRepository regularUserRepository;

    public RegularUserServiceImpl(RegularUserRepository regularUserRepository) {
        this.regularUserRepository = regularUserRepository;
    }

    @Override
    public RegularUser save( RegularUser regularUser) {
//        if (regularUserRepository.findByEmail(regularUser.getEmail()) != null) {
//            throw new IllegalArgumentException("L'email existe déjà.");
//        }
        regularUserRepository.save(regularUser);
        return regularUser;
    }

    @Override
    public RegularUser findById(Long id) {
         return regularUserRepository.findById(id).orElseThrow( () -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    @Override
    public void deleteById(Long id) {
        if (regularUserRepository.existsById(id)) {
            regularUserRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
    }

    @Override
    public RegularUser update(Long id, RegularUser regularUser) {
        RegularUser existingUser = regularUserRepository.findById(id).get();
        if (regularUser.getNom().isEmpty() || regularUser.getPreNom().isEmpty() || regularUser.getType().describeConstable().isEmpty() || regularUser.getTelephone().isEmpty() ) {
            throw new IllegalArgumentException("un champs est vide.");
        }
        BeanUtils.copyProperties(regularUser, existingUser, "Id", "email", "password", "dateNaissance");
        return existingUser;
    }

    @Override
    public RegularUser findByEmail(String email) {
        RegularUser regularUser = regularUserRepository.findByEmail(email);
        if (regularUser != null) {
            return regularUser;
        } else {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
    }


    @Override
    public List<RegularUser> getAll() {
        return regularUserRepository.findAll();
    }

    @Override
    public List voirHistDesDons(Long id) {
        return regularUserRepository.findOrganizationByIdsa(id);
    }

}
