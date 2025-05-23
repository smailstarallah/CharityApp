package ma.emsi.charityapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

public class CreateDonationRequest {
    private Long userId;
    private Long charityId;
    private Double amount;
    private String currency = "EUR";
}
