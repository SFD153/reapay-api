package com.mouni.reapay.models;

import com.mouni.reapay.models.realtorClosingTransaction.BrokerDisbursement;
import com.mouni.reapay.models.realtorClosingTransaction.RealtorDisbursement;
import com.mouni.reapay.models.realtorClosingTransaction.RealtorTransactionDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document("realtor_closing_contract")
@TypeAlias("realtor_closing_contract")
public class RealtorClosingTransaction {

    @Id
    @Generated
    private String id;

    private String realtorEmail;

    private String brokerEmail;

    private String closerEmail;

    private RealtorTransactionDetails transactionDetails;

    private List<RealtorDisbursement> realtorDisbursementList;

    private List<BrokerDisbursement> brokerDisbursementList;

    private String commissionBreakdown;

    private StatusType status;

    private Boolean transactionConfirmed;

    private String bucket;

    private String folder;

    private String pdfName;

}
