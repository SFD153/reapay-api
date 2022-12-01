package com.mouni.reapay.models.requests.realtor;

import com.mouni.reapay.models.realtorClosingTransaction.BrokerDisbursement;
import com.mouni.reapay.models.realtorClosingTransaction.RealtorDisbursement;
import com.mouni.reapay.models.realtorClosingTransaction.RealtorTransactionDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealtorTransactionRequestDto {

    private String realtorEmail;

    private RealtorTransactionDetailsRequestDto transactionDetails;

    private List<RealtorDisbursement> realtorDisbursementList;

    private List<BrokerDisbursement> brokerDisbursementList;

    private String commissionBreakdown;
}
