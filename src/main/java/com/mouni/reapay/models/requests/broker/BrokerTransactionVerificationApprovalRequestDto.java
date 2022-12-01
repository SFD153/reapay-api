package com.mouni.reapay.models.requests.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerTransactionVerificationApprovalRequestDto {

    @NotNull(message = "brokerEmail field is required")
    private String brokerEmail;

    @NotNull(message = "transactionId field is required")
    private String transactionId;
}
