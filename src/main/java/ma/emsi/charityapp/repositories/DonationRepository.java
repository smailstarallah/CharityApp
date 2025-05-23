package ma.emsi.charityapp.repositories;

import jakarta.transaction.Transactional;
import ma.emsi.charityapp.entities.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

@Transactional
public interface DonationRepository extends JpaRepository<Donation, Long> {
    Donation save(Donation donation);
    List findByDateBetween(Date startDate, Date endDate);
    List findByDate(Date date);

}
