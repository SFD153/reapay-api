package com.mouni.reapay.models.responses.realtor;

import com.mouni.reapay.models.StatusType;
import com.mouni.reapay.models.realtorClosingTransaction.BrokerDisbursement;
import com.mouni.reapay.models.realtorClosingTransaction.RealtorDisbursement;
import com.mouni.reapay.models.realtorClosingTransaction.RealtorTransactionDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RealtorClosingTransactionResponseDto {

    private String transactionId;

    private String realtorEmail;

    private String brokerEmail;

    private String closerEmail;

    private RealtorTransactionDetailsResponseDto transactionDetails;

    private List<RealtorDisbursement> realtorDisbursementList;

    private List<BrokerDisbursement> brokerDisbursementList;

    private String commissionBreakdown;

    private StatusType status;

    private Boolean transactionConfirmed;

    private String bucket;

    private String folder;

    private String pdfName;
}
