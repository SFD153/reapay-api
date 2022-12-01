package com.mouni.reapay.services;

import com.mouni.reapay.models.*;
import com.mouni.reapay.models.realtorClosingTransaction.RealtorTransactionDetails;
import com.mouni.reapay.models.requests.realtor.RealtorTransactionRequestDto;
import com.mouni.reapay.models.requests.realtor.RealtorUserCreateRequestDto;
import com.mouni.reapay.models.requests.realtor.RealtorUserLoginRequestDto;
import com.mouni.reapay.models.requests.realtor.RealtorUserUpdateRequestDto;
import com.mouni.reapay.models.responses.realtor.RealtorClosingTransactionResponseDto;
import com.mouni.reapay.models.responses.realtor.RealtorTransactionDetailsResponseDto;
import com.mouni.reapay.models.responses.realtor.RealtorUserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class RealtorUserService {

    private final MongoTemplate mongoTemplate;

    private final EmailService emailService;

    private final JdbcTemplate jdbcTemplate;
    
    private final String EMAIL_VERIFICATION = "Email Verification";

    private final String TRANSACTION_CONFIRMATION = "Confirm your transaction";


    @Autowired
    public RealtorUserService(MongoTemplate mongoTemplate, EmailService emailService, JdbcTemplate jdbcTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void test() {
        String query = DSL.using(SQLDialect.MARIADB)
                .select(DSL.field("first_name"))
                .from("user")
                .getSQL(ParamType.NAMED_OR_INLINED);

        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
        result.forEach(entity -> {
            System.out.println(entity.get("first_name"));
        });

    }
    public RealtorUserResponseDto getRealtor(String realtorEmail){
        RealtorUser realtorUser = findRealtorUserByEmail(realtorEmail);
        if(realtorUser == null ) {
            return null;
        }
        String emailConfirmed = null;
        if (realtorUser.getEmailValidation().equals(false)) {
            emailConfirmed = "Email not confirmed";
        }
        RealtorUserResponseDto responseDto =  convertRealtorUserToResponseDto(realtorUser);
        responseDto.setErrorMessage(emailConfirmed);
        return responseDto;
    }

    public RealtorUserResponseDto checkRealtorUserAndPassword(RealtorUserLoginRequestDto userRequest) {
        RealtorUser realtorUser = findRealtorUserByEmail(userRequest.getEmail());
        if(realtorUser == null ) {
            return null;
        }

        String emailConfirmed = null;
        if (realtorUser.getEmailValidation().equals(false)) {
            emailConfirmed = "Email not confirmed";
        }

        if (BCrypt.checkpw(userRequest.getPassword(), realtorUser.getPassword())) {
            RealtorUserResponseDto responseDto =  convertRealtorUserToResponseDto(realtorUser);
            responseDto.setErrorMessage(emailConfirmed);
            return responseDto;
        }
        return null;
    }

    public RealtorUserResponseDto createRealtorUser(RealtorUserCreateRequestDto userRequest) throws MessagingException {
        RealtorUser realtorUser = findRealtorUserByEmail(userRequest.getEmail());
        if (realtorUser == null) {
            RealtorUser newRealtorUser = RealtorUser.builder()
                    .email(userRequest.getEmail())
                    .userName(userRequest.getUserName())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .nmls(userRequest.getNmls())
                    .password(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()))
                    .emailValidation(false)
                    .build();
            mongoTemplate.save(newRealtorUser);
            emailService.sendEmail("Please, confirm your account <a href=\"http://reapay.io/confirm-realtor/" + newRealtorUser.getId() + "\">here</a>" , EMAIL_VERIFICATION, newRealtorUser.getEmail());
            return convertRealtorUserToResponseDto(newRealtorUser);
        }
        return null;
    }

    public boolean realtorEmailValidation(String realtorUserId) {
        RealtorUser realtorUser = findRealtorUserById(realtorUserId);
        if (realtorUser != null) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(realtorUserId)), Update.update("emailValidation", true), RealtorUser.class);
            return true;
        }
        return false;
    }

    public RealtorUserResponseDto updateRealtorDetails(RealtorUserUpdateRequestDto requestUpdateDto) {
        RealtorUser realtorUser = findRealtorUserByEmail(requestUpdateDto.getEmail());
        if (realtorUser != null) {
            Update update = new Update();
            update.set("brokerName", (requestUpdateDto.getBrokerName() != null) ? requestUpdateDto.getBrokerName() : realtorUser.getBrokerName());
            update.set("company", (requestUpdateDto.getCompany() != null) ? requestUpdateDto.getCompany() : realtorUser.getCompany());
            update.set("managingBroker", (requestUpdateDto.getManagingBroker() != null) ? requestUpdateDto.getManagingBroker() : realtorUser.getManagingBroker());
            update.set("brokerEmail", (requestUpdateDto.getBrokerEmail() != null) ? requestUpdateDto.getBrokerEmail() : realtorUser.getBrokerEmail());
            update.set("brokerPhoneNumber", (requestUpdateDto.getBrokerPhoneNumber() != null) ? requestUpdateDto.getBrokerPhoneNumber() : realtorUser.getBrokerPhoneNumber());
            update.set("closerCompany", (requestUpdateDto.getCloserCompany() != null) ? requestUpdateDto.getCloserCompany() : realtorUser.getCloserCompany());
            update.set("closerName", (requestUpdateDto.getCloserName() != null) ? requestUpdateDto.getCloserName() : realtorUser.getCloserName());
            update.set("closerEmail", (requestUpdateDto.getCloserEmail() != null) ? requestUpdateDto.getCloserEmail() : realtorUser.getCloserEmail());
            update.set("closerPhoneNumber", (requestUpdateDto.getCloserPhoneNumber() != null) ? requestUpdateDto.getCloserPhoneNumber() : realtorUser.getCloserEmail());
            update.set("userName", (requestUpdateDto.getUserName() != null) ? requestUpdateDto.getUserName() : realtorUser.getUserName());
            update.set("nmls", (requestUpdateDto.getNmls() != null) ? requestUpdateDto.getNmls() : realtorUser.getNmls());
            update.set("phoneNumber", (requestUpdateDto.getPhoneNumber() != null) ? requestUpdateDto.getPhoneNumber()  : realtorUser.getPhoneNumber());

            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(realtorUser.getId())), update, RealtorUser.class);

            return convertRealtorUserToResponseDto(findRealtorUserById(realtorUser.getId()));
        }

        return null;
    }

    public RealtorClosingTransactionResponseDto createTransaction(RealtorTransactionRequestDto transactionRequestDto) throws ParseException, MessagingException {
        RealtorUser realtorUser = findRealtorUserByEmail(transactionRequestDto.getRealtorEmail());
        if (realtorUser != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date closingDate = sdf.parse(transactionRequestDto.getTransactionDetails().getClosingDate());
            RealtorTransactionDetails realtorTransactionDetails = RealtorTransactionDetails.builder()
                    .mls(transactionRequestDto.getTransactionDetails().getMls())
                    .propertyAddress(transactionRequestDto.getTransactionDetails().getPropertyAddress())
                    .closingDate(closingDate)
                    .finalPrice(transactionRequestDto.getTransactionDetails().getFinalPrice())
                    .realtorIsRepresenting(transactionRequestDto.getTransactionDetails().getRealtorIsRepresenting())
                    .build();
            RealtorClosingTransaction transaction = RealtorClosingTransaction.builder()
                    .realtorEmail(realtorUser.getEmail())
                    .brokerEmail(realtorUser.getBrokerEmail())
                    .closerEmail(realtorUser.getCloserEmail())
                    .transactionDetails(realtorTransactionDetails)
                    .realtorDisbursementList(transactionRequestDto.getRealtorDisbursementList())
                    .brokerDisbursementList(transactionRequestDto.getBrokerDisbursementList())
                    .commissionBreakdown(transactionRequestDto.getCommissionBreakdown())
                    .transactionConfirmed(false)
                    .build();

            mongoTemplate.save(transaction);

            emailService.sendEmail("Please, confirm your transaction <a href=\"http://www.reapay.io/confirm-transaction-realtor/" + transaction.getId() + "\">here</a>" , TRANSACTION_CONFIRMATION, realtorUser.getEmail());
            emailService.sendEmail(getEmailContentBrokerRequired(realtorUser, transaction),
                    getEmailHeaderBrokerRequired(realtorUser, transaction), realtorUser.getBrokerEmail());

            return convertTransactionToTransactionDto(transaction);
        }
        return null;
    }

    public RealtorClosingTransactionResponseDto getTransaction(String transactionId) {
        RealtorClosingTransaction realtorClosingTransaction = findTransactionById(transactionId);
        if (realtorClosingTransaction != null) {
            return convertTransactionToTransactionDto(realtorClosingTransaction);
        }
        return null;
    }

    public List<RealtorClosingTransactionResponseDto> getTransactionsForRealtor(String realtorEmail) {
        List<RealtorClosingTransaction> transactionList = mongoTemplate.find(Query.query(Criteria.where("realtorEmail").is(realtorEmail)), RealtorClosingTransaction.class);
        List<RealtorClosingTransactionResponseDto> responseDto = new LinkedList<>();
        transactionList.forEach(transaction -> {
            responseDto.add(convertTransactionToTransactionDto(transaction));
        });

        return responseDto;
    }

    public boolean realtorConfirmTransactionClosing(String transactionId) {
        RealtorClosingTransaction closingTransaction = findTransactionById(transactionId);
        if (closingTransaction != null){
            Update update = new Update();
            update.set("transactionConfirmed", true);
            update.set("status", StatusType.PENDING);
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(closingTransaction.getId())), update, RealtorClosingTransaction.class);
        }
        return true;
    }

    public RealtorUser findRealtorUserByEmail(String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), RealtorUser.class);
    }

    private RealtorUser findRealtorUserById(String realtorUserId) {
        return mongoTemplate.findById(realtorUserId, RealtorUser.class);
    }

    public RealtorClosingTransaction findTransactionById(String transactionId) {
        return mongoTemplate.findById(transactionId, RealtorClosingTransaction.class);
    }

    private RealtorUserResponseDto convertRealtorUserToResponseDto(RealtorUser realtorUser) {
        Broker broker = mongoTemplate.findOne(Query.query(Criteria.where("brokerName").is(realtorUser.getBrokerName())), Broker.class);
        Closer closer = mongoTemplate.findOne(Query.query(Criteria.where("closerName").is(realtorUser.getCloserCompany())), Closer.class);
        return RealtorUserResponseDto.builder()
                .brokerName(realtorUser.getBrokerName())
                .noOrderBroker((broker != null) ? broker.getNoOrder() : null)
                .email(realtorUser.getEmail())
                .company(realtorUser.getCompany())
                .nmls(realtorUser.getNmls())
                .phoneNumber(realtorUser.getPhoneNumber())
                .userName(realtorUser.getUserName())
                .managingBroker(realtorUser.getManagingBroker())
                .brokerEmail(realtorUser.getBrokerEmail())
                .brokerPhoneNumber(realtorUser.getBrokerPhoneNumber())
                .closerCompany(realtorUser.getCloserCompany())
                .noOrderCloser((closer != null) ? closer.getNoOrder() : null)
                .closerName(realtorUser.getCloserName())
                .closerEmail(realtorUser.getCloserEmail())
                .closerPhoneNumber(realtorUser.getPhoneNumber())
                .emailValidation(realtorUser.getEmailValidation())
                .userType(UserType.REALTOR)
                .build();
    }

    public RealtorClosingTransactionResponseDto convertTransactionToTransactionDto(RealtorClosingTransaction transaction) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String closingDate = sdf.format(transaction.getTransactionDetails().getClosingDate());
        RealtorTransactionDetailsResponseDto transactionDetails = RealtorTransactionDetailsResponseDto.builder().
                mls(transaction.getTransactionDetails().getMls())
                .propertyAddress(transaction.getTransactionDetails().getPropertyAddress())
                .closingDate(closingDate)
                .finalPrice(transaction.getTransactionDetails().getFinalPrice())
                .realtorIsRepresenting(transaction.getTransactionDetails().getRealtorIsRepresenting())
                .build();
        return RealtorClosingTransactionResponseDto.builder()
                .transactionId(transaction.getId())
                .realtorEmail(transaction.getRealtorEmail())
                .brokerEmail(transaction.getBrokerEmail())
                .closerEmail(transaction.getCloserEmail())
                .transactionDetails(transactionDetails)
                .realtorDisbursementList(transaction.getRealtorDisbursementList())
                .brokerDisbursementList(transaction.getBrokerDisbursementList())
                .commissionBreakdown(transaction.getCommissionBreakdown())
                .transactionConfirmed(transaction.getTransactionConfirmed())
                .status(transaction.getStatus())
                .bucket(transaction.getBucket())
                .folder(transaction.getFolder())
                .pdfName(transaction.getPdfName())
                .build();
    }

    private String getEmailContentBrokerRequired(RealtorUser realtorUser, RealtorClosingTransaction transaction) {
        Closer closer = mongoTemplate.findOne(Query.query(Criteria.where("closerName").is(realtorUser.getCloserCompany())), Closer.class);
        return "Hi " + realtorUser.getManagingBroker() + "<br> <br>" +
                "I've submitted a request for a Direct Commission Disbursement Authorization through "
                + realtorUser.getCloserName() + " at " + realtorUser.getCloserCompany() + " for my closing on "
                + transaction.getTransactionDetails().getClosingDate() + ". <br> <br>" +
                realtorUser.getCompany() + "<br>" +
                "Closer: " + realtorUser.getCloserName() + "<br>" +
                "Phone: " + realtorUser.getCloserPhoneNumber() + "<br>" +
                "Address: " + ((realtorUser.getCloserCompany() != null) ? realtorUser.getCloserCompany() : null)   + "<br><br>" +
                "If you could kindly <a href=\"http://www.reapay.io\">approve this request</a> that would be greatly appreciated. <br> <br>" +
                "Best, <br> <br>" +
                realtorUser.getUserName() + "<br>" +
                realtorUser.getCompany() + "<br>" +
                realtorUser.getPhoneNumber() + "<br>";
    }

    private String getEmailHeaderBrokerRequired(RealtorUser realtorUser, RealtorClosingTransaction transaction) {
        return realtorUser.getManagingBroker() + ", " + realtorUser.getUserName() + " is requesting a Direct Commission Disbursement";
    }

}
