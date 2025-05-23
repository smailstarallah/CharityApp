package ma.emsi.charityapp.dto;

import lombok.Data;

@Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ExecutePaymentRequest {
    private String paymentId;
    private String payerId;
    private Long userId;
    private Long charityId;
    private Double amount;
}
