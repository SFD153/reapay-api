package com.mouni.reapay.controllers;

import com.mouni.reapay.models.requests.broker.BrokerConfirmTransactionApprovalRequestDto;
import com.mouni.reapay.models.requests.broker.BrokerTransactionVerificationApprovalRequestDto;
import com.mouni.reapay.models.requests.broker.BrokerUserCreateRequestDto;
import com.mouni.reapay.models.requests.broker.BrokerUserLoginRequestDto;
import com.mouni.reapay.models.responses.broker.BrokerUserResponseDto;
import com.mouni.reapay.models.responses.realtor.RealtorClosingTransactionResponseDto;
import com.mouni.reapay.services.BrokerUserService;
import com.mouni.reapay.services.RealtorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/v1/user/broker")
public class BrokerUserController {

    private final BrokerUserService brokerUserService;

    private final RealtorUserService realtorUserService;

    private static final String statusHeaderName = "Status";

    @Autowired
    public BrokerUserController(BrokerUserService brokerUserService, RealtorUserService realtorUserService){
        this.brokerUserService = brokerUserService;
        this.realtorUserService = realtorUserService;
    }


    @GetMapping("")
    public ResponseEntity<Object> getBroker(HttpServletRequest request, @RequestParam String brokerEmail ){
        try {
            BrokerUserResponseDto responseUser = brokerUserService.getBroker(brokerEmail);
            if (responseUser != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "User found");
                return new ResponseEntity<Object>(responseUser, headers, HttpStatus.OK);
            } else {
                return throwError("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginBrokerUser(HttpServletRequest request, @RequestBody BrokerUserLoginRequestDto brokerUserRequest) {
        try {
            BrokerUserResponseDto responseUser = brokerUserService.checkBrokerUserAndPassword(brokerUserRequest);
            if (responseUser != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "User found and logged in");
                return new ResponseEntity<Object>(responseUser, headers, HttpStatus.OK);
            } else {
                return throwError("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> createBrokerUser(HttpServletRequest request, @RequestBody BrokerUserCreateRequestDto brokerUserRequest) {
        try {
            BrokerUserResponseDto responseUser = brokerUserService.createBrokerUser(brokerUserRequest);
            if(responseUser != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "User Registered");
                return new ResponseEntity<Object>(responseUser, headers, HttpStatus.OK);
            } else {
                return throwError("User already exist", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/email-validation")
    public ResponseEntity<Object> emailValidation(HttpServletRequest request, @RequestParam String brokerUserId) {
        try {
            boolean responseValidation = brokerUserService.brokerEmailValidation(brokerUserId);
            if (responseValidation) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Email Validated");
                return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Broker User Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Object> throwError(String headerValue, HttpStatus httpStatus){
        HttpHeaders headers = new HttpHeaders();
        headers.add(statusHeaderName, headerValue);
        return new ResponseEntity<>(null, headers, httpStatus);
    }

    @GetMapping("/transaction")
    public ResponseEntity<Object> getTransaction(HttpServletRequest request, @RequestParam String transactionId) {
        try {
            RealtorClosingTransactionResponseDto transactionResponse = realtorUserService.getTransaction(transactionId);
            if (transactionResponse != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Transaction Found");
                return new ResponseEntity<Object>(transactionResponse, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Transaction Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-transactions")
    public ResponseEntity<Object> getAllTransaction(HttpServletRequest request, @RequestParam String brokerEmail) {
        try {
            List<RealtorClosingTransactionResponseDto> transactionListResponse = brokerUserService.getTransactionsForBrokerToApprove(brokerEmail);
            HttpHeaders headers = new HttpHeaders();
            headers.add(statusHeaderName, "Transactions Found For Current User Email");
            HashMap<String, List<RealtorClosingTransactionResponseDto>> responseTransaction  = new LinkedHashMap<>();
            responseTransaction.put("transactions", transactionListResponse);
            return new ResponseEntity<Object>(responseTransaction, headers, HttpStatus.OK);
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/send-transaction-approval")
    public ResponseEntity<Object> sendTransactionVerificationApproval(HttpServletRequest request, @RequestBody BrokerTransactionVerificationApprovalRequestDto requestTransactionApproval){
        try {
            boolean responseValidation = brokerUserService.sendTransactionVerificationApproval(requestTransactionApproval.getBrokerEmail(), requestTransactionApproval.getTransactionId());
            if (responseValidation) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Email Sent for Transaction Approval");
                return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Transaction Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transaction-approve")
    public ResponseEntity<Object> confirmTransactionApproval(HttpServletRequest request, @RequestParam String transactionId){
        try {
            boolean responseValidation = brokerUserService.transactionApproved(transactionId);
            if (responseValidation) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Transaction Approved ");
                return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Transaction Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
