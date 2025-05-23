package ma.emsi.charityapp.services;


import ma.emsi.charityapp.entities.Donation;
import ma.emsi.charityapp.repositories.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DonationServiceImpl implements DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Override
    public Donation addDonation(Donation donation) {
        Donation don = donationRepository.save(donation);
        return don;
    }

    @Override
    public void updateDonation(Donation donation) {

    }

    @Override
    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }

    @Override
    public Donation getDonationById(Long id) {
        return donationRepository.findById(id).get();
    }

    @Override
    public List<Donation> getAllDonations() {
        System.out.println("====================");
        System.out.println(donationRepository.findAll());

        return donationRepository.findAll();
    }

    @Override
    public List<Donation> getDonationsByDate(Date date) {
        return donationRepository.findByDate(date);
    }

    @Override
    public List<Donation> getDonationsBetweenDates(Date startDate, Date endDate) {
        return donationRepository.findByDateBetween(startDate, endDate);
    }
}
