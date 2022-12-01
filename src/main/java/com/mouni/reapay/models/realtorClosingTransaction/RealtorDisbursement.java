package com.mouni.reapay.models.realtorClosingTransaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RealtorDisbursement {

    private String detailsPayment;

    private String receiverName;

    private String amount;

}
