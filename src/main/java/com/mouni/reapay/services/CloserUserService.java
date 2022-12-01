package com.mouni.reapay.services;

import com.mouni.reapay.models.*;
import com.mouni.reapay.models.requests.broker.BrokerUserCreateRequestDto;
import com.mouni.reapay.models.requests.closer.CloserUserCreateRequestDto;
import com.mouni.reapay.models.requests.closer.CloserUserLoginRequestDto;
import com.mouni.reapay.models.requests.closer.ResponseTextTransaction;
import com.mouni.reapay.models.requests.realtor.RealtorUserLoginRequestDto;
import com.mouni.reapay.models.responses.broker.BrokerUserResponseDto;
import com.mouni.reapay.models.responses.closer.CloserUserResponseDto;
import com.mouni.reapay.models.responses.realtor.RealtorClosingTransactionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.print.attribute.standard.Media;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class CloserUserService {

    private final MongoTemplate mongoTemplate;

    private final EmailService emailService;

    private final RealtorUserService realtorUserService;

    private final MediaUtilsService mediaUtilsService;

    private final String EMAIL_VERIFICATION = "Email Verification";

    private final String TRANSACTION_CONFIRMATION_CLOSING = "Confirm your transaction closing";

    @Autowired
    public CloserUserService(MongoTemplate mongoTemplate, EmailService emailService, RealtorUserService realtorUserService, MediaUtilsService mediaUtilsService) {
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;
        this.realtorUserService = realtorUserService;
        this.mediaUtilsService = mediaUtilsService;
    }

    public CloserUserResponseDto getCloser(String closerEmail){
        CloserUser closerUser = findCloserUserByEmail(closerEmail);
        if(closerUser == null){
            return null;
        }
        String emailConfirmed = null;
        if(closerUser.getEmailValidation().equals(false)){
            emailConfirmed = "Email not confirmed";
        }
        CloserUserResponseDto responseDto = convertCloserUserToResponseDto(closerUser);
        responseDto.setErrorMessage(emailConfirmed);
        return responseDto;
    }

    public CloserUserResponseDto checkCloserUserAndPassword(CloserUserLoginRequestDto userRequest){
        CloserUser closerUser = findCloserUserByEmail(userRequest.getEmail());
        if(closerUser == null){
            return null;
        }

        String emailConfirmed = null;
        if(closerUser.getEmailValidation().equals(false)){
            emailConfirmed = "Email not confirmed";
        }

        if (BCrypt.checkpw(userRequest.getPassword(), closerUser.getPassword())) {
            CloserUserResponseDto responseDto = convertCloserUserToResponseDto(closerUser);
            responseDto.setErrorMessage(emailConfirmed);
            return responseDto;
        }
        return null;
    }

    public CloserUserResponseDto createCloserUser(CloserUserCreateRequestDto userRequest) throws MessagingException {
        CloserUser closerUser = findCloserUserByEmail(userRequest.getEmail());
        if (closerUser == null) {
            CloserUser newCloserUser = CloserUser.builder()
                    .email(userRequest.getEmail())
                    .userName(userRequest.getUserName())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .closerCompanyName(userRequest.getCloserCompanyName())
                    .password(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()))
                    .emailValidation(false)
                    .build();
            mongoTemplate.save(newCloserUser);
            emailService.sendEmail("Please, confirm your account <a href=\"http://reapay.io/confirm-closer/" + newCloserUser.getId() + "\">here</a>" , EMAIL_VERIFICATION, newCloserUser.getEmail());
            return convertCloserUserToResponseDto(newCloserUser);
        }
        return null;
    }

    public boolean closerEmailValidation(String closerUserId) {
        CloserUser closerUser = findCloserUserById(closerUserId);
        if (closerUser != null) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(closerUserId)), Update.update("emailValidation", true), CloserUser.class);
            return true;
        }
        return false;
    }

    public List<RealtorClosingTransactionResponseDto> getTransactionsForCloserToApprove(String closerEmail) {
        List<RealtorClosingTransaction> transactionList = mongoTemplate.find(Query.query(Criteria.where("closerEmail").is(closerEmail)), RealtorClosingTransaction.class);

        List<RealtorClosingTransactionResponseDto> responseDto = new LinkedList<>();
        transactionList.forEach(transaction -> {
            responseDto.add(realtorUserService.convertTransactionToTransactionDto(transaction));
        });

        return responseDto;
    }
    private CloserUser findCloserUserById(String brokerUserId) {
        return mongoTemplate.findById(brokerUserId, CloserUser.class);
    }

    private CloserUser findCloserUserByEmail(String email){
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), CloserUser.class);
    }


    private CloserUserResponseDto convertCloserUserToResponseDto(CloserUser closerUser){
        return CloserUserResponseDto.builder()
                .closerCompanyName(closerUser.getCloserCompanyName())
                .email(closerUser.getEmail())
                .phoneNumber(closerUser.getPhoneNumber())
                .userName(closerUser.getUserName())
                .userType(UserType.CLOSER)
                .build();
    }

    public boolean sendTransactionVerificationClosing(String closerEmail, String transactionId) throws MessagingException {
        RealtorClosingTransaction transaction = realtorUserService.findTransactionById(transactionId);
        if(transaction != null) {
            emailService.sendEmail("Please, confirm your transaction closing <a href=\"http://www.reapay.io/confirm-transaction-closer/" + transaction.getId() + "\">here</a>" , TRANSACTION_CONFIRMATION_CLOSING, closerEmail);
            return true;
        }
        return false;
    }

    public boolean transactionClosing(String transactionId) {
        RealtorClosingTransaction transaction = realtorUserService.findTransactionById(transactionId);
        if(transaction != null) {
            //mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(transactionId).and("brokerEmail").is(brokerEmail)), Update.update("status", StatusType.ACCEPTED), RealtorClosingTransaction.class);
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(transactionId)), Update.update("status", StatusType.CLOSED), RealtorClosingTransaction.class);
            return true;
        }
        return false;
    }

    public ResponseTextTransaction responseDataForCloser(String transactionId) throws ParseException {
        RealtorClosingTransaction transaction = realtorUserService.findTransactionById(transactionId);
        RealtorUser realtorUser = realtorUserService.findRealtorUserByEmail(transaction.getRealtorEmail());
        Broker brokerCompany = mongoTemplate.findOne(Query.query(Criteria.where("brokerName").is(realtorUser.getBrokerName())), Broker.class);
        Closer closerCompany = mongoTemplate.findOne(Query.query(Criteria.where("closerName").is(realtorUser.getCloserCompany())), Closer.class);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String closingDate = sdf.format(transaction.getTransactionDetails().getClosingDate());
        ResponseTextTransaction responseTextTransaction = ResponseTextTransaction.builder()
                .transactionId(transactionId)
                .closerCompanyName(realtorUser.getCloserCompany())
                .closerCompanyAddress(closerCompany.getCloserAddress())
                .closerName(realtorUser.getCloserName())
                .closerEmail(realtorUser.getCloserEmail())
                .closerPhone(realtorUser.getCloserPhoneNumber())
                .closingDate(closingDate)
                .realtorName(realtorUser.getUserName())
                .brokerCompanyName(realtorUser.getBrokerName())
                .mls(transaction.getTransactionDetails().getMls())
                .propertyAddress(transaction.getTransactionDetails().getPropertyAddress())
                .firstParagraph(mediaUtilsService.generateFirstParagraph(realtorUser, transaction))
                .secondParagraph(mediaUtilsService.generateSecondParagraph(realtorUser, transaction, brokerCompany))
                .thirdParagraph(mediaUtilsService.generateThirdParagraph(realtorUser, transaction, brokerCompany))
                .fourthParagraph(mediaUtilsService.generateFourthParagraph(realtorUser, transaction, brokerCompany))
                .fifthParagraph(mediaUtilsService.generateFifthParagraph(realtorUser, transaction))
                .build();

        return responseTextTransaction;
    }
}
