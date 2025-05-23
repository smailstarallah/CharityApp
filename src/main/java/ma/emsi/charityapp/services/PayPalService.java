package ma.emsi.charityapp.services;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import ma.emsi.charityapp.dto.CreateDonationRequest;

import java.math.BigDecimal;

public interface PayPalService {
    Payment createPayment(CreateDonationRequest request) throws PayPalRESTException ;
    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException ;
}
