package com.mouni.reapay.services;

import com.mouni.reapay.models.*;
import com.mouni.reapay.models.requests.broker.BrokerUserCreateRequestDto;
import com.mouni.reapay.models.requests.broker.BrokerUserLoginRequestDto;
import com.mouni.reapay.models.responses.broker.BrokerUserResponseDto;
import com.mouni.reapay.models.responses.realtor.RealtorClosingTransactionResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.LinkedList;
import java.util.List;

@Service
public class BrokerUserService {

    private final MongoTemplate mongoTemplate;

    private final EmailService emailService;

    private final RealtorUserService realtorUserService;

    private final String EMAIL_VERIFICATION = "Email Verification";

    private final String TRANSACTION_CONFIRMATION_APPROVAL = "Confirm your transaction approval";


    @Autowired
    public BrokerUserService(MongoTemplate mongoTemplate, EmailService emailService, RealtorUserService realtorUserService) {
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;
        this.realtorUserService = realtorUserService;
    }

    public BrokerUserResponseDto getBroker(String brokerEmail) {
        BrokerUser brokerUser = findBrokerUserByEmail(brokerEmail);
        if (brokerUser == null) {
            return null;
        }

        String emailConfirmed = null;
        if (brokerUser.getEmailValidation().equals(false)) {
            emailConfirmed = "Email not confirmed";
        }

        BrokerUserResponseDto responseDto = convertBrokerUserToResponseDto(brokerUser);
        responseDto.setErrorMessage(emailConfirmed);
        return responseDto;
    }

    public BrokerUserResponseDto checkBrokerUserAndPassword(BrokerUserLoginRequestDto userRequest) {
        BrokerUser brokerUser = findBrokerUserByEmail(userRequest.getEmail());
        if (brokerUser == null) {
            return null;
        }
        String emailConfirmed = null;
        if (brokerUser.getEmailValidation().equals(false)) {
            emailConfirmed = "Email not confirmed";
        }

        if (BCrypt.checkpw(userRequest.getPassword(), brokerUser.getPassword())) {
            BrokerUserResponseDto responseDto = convertBrokerUserToResponseDto(brokerUser);
            responseDto.setErrorMessage(emailConfirmed);
            return responseDto;
        }
        return null;
    }


    public BrokerUserResponseDto createBrokerUser(BrokerUserCreateRequestDto userRequest) throws MessagingException {
        BrokerUser brokerUser = findBrokerUserByEmail(userRequest.getEmail());
        if (brokerUser == null) {
            BrokerUser newBrokerUser = BrokerUser.builder()
                    .email(userRequest.getEmail())
                    .userName(userRequest.getUserName())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .nrds(userRequest.getNrds())
                    .password(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()))
                    .emailValidation(false)
                    .build();
            mongoTemplate.save(newBrokerUser);
            emailService.sendEmail("Please, confirm your account <a href=\"http://reapay.io/confirm-broker/" + newBrokerUser.getId() + "\">here</a>" , EMAIL_VERIFICATION, newBrokerUser.getEmail());
            return convertBrokerUserToResponseDto(newBrokerUser);
        }
        return null;
    }

    public boolean brokerEmailValidation(String brokerUserId) {
        BrokerUser brokerUser = findBrokerUserById(brokerUserId);
        if (brokerUser != null) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(brokerUserId)), Update.update("emailValidation", true), BrokerUser.class);
            return true;
        }
        return false;
    }

    public BrokerUser findBrokerUserByEmail(String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), BrokerUser.class);
    }

    private BrokerUser findBrokerUserById(String brokerUserId) {
        return mongoTemplate.findById(brokerUserId, BrokerUser.class);
    }

    private BrokerUserResponseDto convertBrokerUserToResponseDto(BrokerUser brokerUser) {
        return BrokerUserResponseDto.builder()
                .brokerCompanyName(brokerUser.getBrokerCompanyName())
                .email(brokerUser.getEmail())
                .phoneNumber(brokerUser.getPhoneNumber())
                .userName(brokerUser.getUserName())
                .nrds(brokerUser.getNrds())
                .userType(UserType.BROKER)
                .build();
    }

    public List<RealtorClosingTransactionResponseDto> getTransactionsForBrokerToApprove(String brokerEmail) {
        List<RealtorClosingTransaction> transactionList = mongoTemplate.find(Query.query(Criteria.where("brokerEmail").is(brokerEmail)), RealtorClosingTransaction.class);

        List<RealtorClosingTransactionResponseDto> responseDto = new LinkedList<>();
        transactionList.forEach(transaction -> {
            responseDto.add(realtorUserService.convertTransactionToTransactionDto(transaction));
        });

        return responseDto;
    }


    public boolean sendTransactionVerificationApproval(String brokerEmail, String transactionId) throws MessagingException {
        RealtorClosingTransaction transaction = realtorUserService.findTransactionById(transactionId);
        if (transaction != null) {
            RealtorUser realtorUser = realtorUserService.findRealtorUserByEmail(transaction.getRealtorEmail());
            emailService.sendEmail("Please, confirm your transaction approval <a href=\"http://www.reapay.io/confirm-transaction-broker/" + transaction.getId() + "\">here</a>" , TRANSACTION_CONFIRMATION_APPROVAL , realtorUser.getBrokerEmail());
            return true;
        }
        return false;
    }

    public boolean transactionApproved(String transactionId) throws MessagingException {
        RealtorClosingTransaction transaction = realtorUserService.findTransactionById(transactionId);
        if (transaction != null) {
            //mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(transactionId).and("brokerEmail").is(brokerEmail)), Update.update("status", StatusType.ACCEPTED), RealtorClosingTransaction.class);
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(transactionId)), Update.update("status", StatusType.ACCEPTED), RealtorClosingTransaction.class);
            RealtorUser realtorUser = realtorUserService.findRealtorUserByEmail(transaction.getRealtorEmail());
            emailService.sendEmail(getEmailContentCloserRequired(realtorUser, transaction), getEmailHeaderCloserRequired(realtorUser, transaction), realtorUser.getCloserEmail());
            return true;
        }
        return false;
    }

    private String getEmailContentCloserRequired(RealtorUser realtorUser, RealtorClosingTransaction transaction) {
        Closer closer = mongoTemplate.findOne(Query.query(Criteria.where("closerName").is(realtorUser.getCloserCompany())), Closer.class);
        return "Hi " + realtorUser.getCloserName() + "<br> <br>" +
                realtorUser.getManagingBroker() + " has signed a Direct Commission Disbursement Authorization for " + realtorUser.getUserName() + ". <br> <br>" +
                "MLS #: " + transaction.getTransactionDetails().getMls() + "<br>" +
                "Address: " + transaction.getTransactionDetails().getPropertyAddress() + "<br>" +
                "Closing: " + transaction.getTransactionDetails().getClosingDate() + "<br> <br>" +
                "To view the full details of this approval please <a href=\"http://www.reapay.io/login\">login</a> or <a href=\"http://www.reapay.io/sign-up\">create an account</a>. <br> <br>" +
                "Best, <br> <br>" +
                realtorUser.getManagingBroker() + "<br>" +
                realtorUser.getBrokerName() + "<br>" +
                realtorUser.getPhoneNumber() + "<br>";

    }

    private String getEmailHeaderCloserRequired(RealtorUser realtorUser, RealtorClosingTransaction transaction) {
        return realtorUser.getManagingBroker() + ", " + realtorUser.getUserName() + " has approved " + realtorUser.getUserName() + " for a Direct Commission Disbursement.";
    }
}
