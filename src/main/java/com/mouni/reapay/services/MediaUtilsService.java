package com.mouni.reapay.services;

import com.mouni.reapay.models.*;
import com.mouni.reapay.models.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Slf4j
@Service
public class MediaUtilsService {

    private final MongoTemplate mongoTemplate;

    private final PdfService pdfService;

    private final RealtorUserService realtorUserService;

    private final BrokerUserService brokerUserService;

    private final NumberToWordsService numberToWordsService;

    private final AwsService awsService;
    @Autowired
    public MediaUtilsService(AwsService awsService, MongoTemplate mongoTemplate, PdfService pdfService, RealtorUserService realtorUserService, BrokerUserService brokerUserService, NumberToWordsService numberToWordsService) {
        this.mongoTemplate = mongoTemplate;
        this.pdfService = pdfService;
        this.realtorUserService = realtorUserService;
        this.brokerUserService = brokerUserService;
        this.numberToWordsService = numberToWordsService;
        this.awsService = awsService;
    }

    public void createPdf(String transactionId) throws ParseException {
        awsService.downloadTransactionTemplatePdfS3();
        System.out.println("Incep sa creez pdf");
        RealtorClosingTransaction transaction = realtorUserService.findTransactionById(transactionId);
        RealtorUser realtorUser = realtorUserService.findRealtorUserByEmail(transaction.getRealtorEmail());
        Broker brokerCompany = mongoTemplate.findOne(Query.query(Criteria.where("brokerName").is(realtorUser.getBrokerName())), Broker.class);
        Closer closerCompany = mongoTemplate.findOne(Query.query(Criteria.where("closerName").is(realtorUser.getCloserCompany())), Closer.class);
        Broker failoverBroker = Broker.builder()
                .id("failover")
                .brokerName("FailOver Broker")
                .brokerAddress("FailOver Address")
                .noOrder(0).build();
        Closer failoverCloser = Closer.builder()
                .id("failover")
                .closerName("FailOver Closer")
                .closerAddress("FailOver Address")
                .noOrder(0).build();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String closingDate = sdf.format(transaction.getTransactionDetails().getClosingDate());
        PdfRequest pdfRequest = PdfRequest.builder()
                .transactionId(transactionId)
                .closerCompanyName(realtorUser.getCloserCompany())
                .closerCompanyAddress((closerCompany != null)? closerCompany.getCloserAddress() : "FailOver Address")
                .closerName(realtorUser.getCloserName())
                .closerEmail(realtorUser.getCloserEmail())
                .closerPhone(realtorUser.getCloserPhoneNumber())
                .closingDate(closingDate)
                .realtorName(realtorUser.getUserName())
                .brokerCompanyName(realtorUser.getBrokerName())
                .mls(transaction.getTransactionDetails().getMls())
                .propertyAddress(transaction.getTransactionDetails().getPropertyAddress())
                .firstParagraph(generateFirstParagraph(realtorUser, transaction))
                .secondParagraph(generateSecondParagraph(realtorUser, transaction, (brokerCompany != null)? brokerCompany : failoverBroker))
                .thirdParagraph(generateThirdParagraph(realtorUser, transaction, (brokerCompany != null)? brokerCompany : failoverBroker))
                .fourthParagraph(generateFourthParagraph(realtorUser, transaction, (brokerCompany != null)? brokerCompany : failoverBroker))
                .fifthParagraph(generateFifthParagraph(realtorUser, transaction))
                .build();

        System.out.println("trimit mai departe");
        pdfService.createPdf(pdfRequest);
    }



    public String generateFirstParagraph(RealtorUser realtorUser, RealtorClosingTransaction transaction) throws ParseException {
        Pair<String, String> moneyAmountWritten = getWrittenNumber(transaction.getRealtorDisbursementList().get(0).getAmount());
        return realtorUser.getCloserCompany() + " is hereby instructed to deduct " + transaction.getRealtorDisbursementList().get(0).getAmount() + " (" + moneyAmountWritten.getFirst() + " and " + moneyAmountWritten.getSecond() + ") " + "from the commission proceeds that would be otherwise payable to " + realtorUser.getManagingBroker() + ". And to remit this amount to " + transaction.getRealtorDisbursementList().get(0).getReceiverName() + " at closing. These instructions can be changed at anytime prior to successful close of escrow. This shall be your good and sufficient authority to make this disbursement to " + transaction.getRealtorDisbursementList().get(0).getReceiverName() + ".\n";
    }

    public String generateSecondParagraph(RealtorUser realtorUser, RealtorClosingTransaction transaction, Broker brokerCompany) throws ParseException {
        Pair<String, String> moneyAmountWritten = getWrittenNumber(transaction.getBrokerDisbursementList().get(0).getAmount());
        return  realtorUser.getCloserCompany() + " is hereby instructed to disburse Commission proceeds in the amount of " + transaction.getBrokerDisbursementList().get(0).getAmount() + " (" + moneyAmountWritten.getFirst() + " and " + moneyAmountWritten.getSecond() +") made payable to " + realtorUser.getManagingBroker() + ". This check will be mailed to " + realtorUser.getManagingBroker() + " at " + brokerCompany.getBrokerAddress() + ".\n";
    }

    public String generateThirdParagraph(RealtorUser realtorUser, RealtorClosingTransaction transaction, Broker brokerCompany) throws ParseException {
        Pair<String, String> moneyAmountWritten = getWrittenNumber(transaction.getBrokerDisbursementList().get(1).getAmount());
        return  realtorUser.getCloserCompany() + " is hereby instructed to disburse an Admin Fee in the amount of " + transaction.getBrokerDisbursementList().get(1).getAmount() +  " (" + moneyAmountWritten.getFirst() + " and " + moneyAmountWritten.getSecond() + ") made payable to " + realtorUser.getCloserCompany() + ". This check will be mailed to " +  realtorUser.getManagingBroker() + " at " + brokerCompany.getBrokerAddress() +".\n";
    }

    public String generateFourthParagraph(RealtorUser realtorUser, RealtorClosingTransaction transaction, Broker brokerCompany) {
        return "A copy of the FINAL ALTA will be mailed to "  + realtorUser.getManagingBroker() + " at " + brokerCompany.getBrokerAddress() + ".\n";
    }

    public String generateFifthParagraph(RealtorUser realtorUser, RealtorClosingTransaction transaction) {
        return "In the event of changes are made to closing, causing the Amount to be Disbursed by " + realtorUser.getManagingBroker() + " to change, I hereby authorizes " + realtorUser.getCloserCompany() + " to provide amended/updated disbursement instructions as required.\n";
    }

    private Pair<String, String> getWrittenNumber(String moneyAmountAsString) throws ParseException {
        String [] moneyAmountParts = moneyAmountAsString.split("\\.");
        String firstValue = moneyAmountParts[0];
        String secondValue = moneyAmountParts[1];
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        long firstMoneyAmount = format.parse(firstValue).longValue();
        long secondMoneyAmount = Long.parseLong(secondValue);
        String firstMoneyValueWritten = NumberToWordsService.convert(firstMoneyAmount);
        String secondMoneyValueWritten = NumberToWordsService.convert(secondMoneyAmount);
        return new Pair<>(WordUtils.capitalizeFully(firstMoneyValueWritten), WordUtils.capitalizeFully(secondMoneyValueWritten));
    }
}

