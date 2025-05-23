package ma.emsi.charityapp.services;


import ma.emsi.charityapp.entities.Donation;
import java.util.Date;
import java.util.List;

public interface DonationService {
    Donation addDonation(Donation donation);
    void updateDonation(Donation donation);
    void deleteDonation(Long id);
    Donation getDonationById(Long id);
    List<Donation> getAllDonations();
    List<Donation> getDonationsByDate(Date date);
    List<Donation> getDonationsBetweenDates(Date startDate, Date endDate);
}
