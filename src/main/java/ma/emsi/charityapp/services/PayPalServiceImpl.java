package ma.emsi.charityapp.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.charityapp.dto.CreateDonationRequest;
import ma.emsi.charityapp.entities.Organization;
import ma.emsi.charityapp.repositories.CharityActionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class PayPalServiceImpl implements PayPalService {

    private APIContext apiContext;
    CharityActionRepository charityActionRepository;

    public PayPalServiceImpl(APIContext apiContext, CharityActionRepository charityActionRepository) {
        this.apiContext = apiContext;
        this.charityActionRepository = charityActionRepository;
    }

    public Payment createPayment(CreateDonationRequest request) throws PayPalRESTException {
        Organization organization = charityActionRepository.findById(request.getCharityId()).get().getOrganization();

        Amount amount = new Amount();
        amount.setCurrency(request.getCurrency());
        amount.setTotal(String.format(Locale.forLanguageTag("en-US"), "%.2f", request.getAmount()));

        // Définir le bénéficiaire (l'organisation)
        Payee payee = new Payee();
        payee.setEmail(organization.getPaypalEmail());

        Transaction transaction = new Transaction();
        transaction.setDescription("Don pour " + organization.getNom());
        transaction.setAmount(amount);
        transaction.setPayee(payee); // ← L'argent va directement à l'organisation

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:5173/donation/cancel");
        redirectUrls.setReturnUrl("http://localhost:5173/donation/success?userId=" + request.getUserId() + "&orgId=" + charityActionRepository.findById(request.getCharityId()).get().getOrganization().getId() + "&amount=" + request.getAmount());
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        log.info("Executing payment with ID: {}", paymentId);
        return payment.execute(apiContext, paymentExecute);
    }
}
