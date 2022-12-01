package com.mouni.reapay.models.requests.closer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloserTransactionVerificationClosingRequestDto {
    @NotNull(message = "closerEmail field is required")
    private String closerEmail;

    @NotNull(message = "transactionId field is required")
    private String transactionId;
}
