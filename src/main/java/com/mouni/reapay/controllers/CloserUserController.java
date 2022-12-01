package com.mouni.reapay.controllers;

import com.mouni.reapay.models.requests.broker.BrokerTransactionVerificationApprovalRequestDto;
import com.mouni.reapay.models.requests.broker.BrokerUserCreateRequestDto;
import com.mouni.reapay.models.requests.broker.BrokerUserLoginRequestDto;
import com.mouni.reapay.models.requests.closer.CloserTransactionVerificationClosingRequestDto;
import com.mouni.reapay.models.requests.closer.CloserUserCreateRequestDto;
import com.mouni.reapay.models.requests.closer.CloserUserLoginRequestDto;
import com.mouni.reapay.models.requests.closer.ResponseTextTransaction;
import com.mouni.reapay.models.responses.broker.BrokerUserResponseDto;
import com.mouni.reapay.models.responses.closer.CloserUserResponseDto;
import com.mouni.reapay.models.responses.realtor.RealtorClosingTransactionResponseDto;
import com.mouni.reapay.services.CloserUserService;
import com.mouni.reapay.services.RealtorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/user/closer")
public class CloserUserController {

    private final CloserUserService closerUserService;

    private final RealtorUserService realtorUserService;

    private static final String statusHeaderName = "Status";

    @Autowired
    public CloserUserController(CloserUserService closerUserService, RealtorUserService realtorUserService) {
        this.closerUserService = closerUserService;
        this.realtorUserService = realtorUserService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getCloser(HttpServletRequest request, @RequestParam String closerEmail ){
        try {
            CloserUserResponseDto responseUser = closerUserService.getCloser(closerEmail);
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
    public ResponseEntity<Object> loginCloserUser(HttpServletRequest request, @RequestBody CloserUserLoginRequestDto closerUserRequest) {
        try {
            CloserUserResponseDto responseUser = closerUserService.checkCloserUserAndPassword(closerUserRequest);
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
    public ResponseEntity<Object> createBrokerUser(HttpServletRequest request, @RequestBody CloserUserCreateRequestDto closerUserRequest) {
        try {
            CloserUserResponseDto responseUser = closerUserService.createCloserUser(closerUserRequest);
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
    public ResponseEntity<Object> emailValidation(HttpServletRequest request, @RequestParam String closerUserId) {
        try {
            boolean responseValidation = closerUserService.closerEmailValidation(closerUserId);
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
    public ResponseEntity<Object> getAllTransaction(HttpServletRequest request, @RequestParam String closerEmail) {
        try {
            List<RealtorClosingTransactionResponseDto> transactionListResponse = closerUserService.getTransactionsForCloserToApprove(closerEmail);
            HttpHeaders headers = new HttpHeaders();
            headers.add(statusHeaderName, "Transactions Found For Current User Email");
            HashMap<String, List<RealtorClosingTransactionResponseDto>> responseTransaction  = new LinkedHashMap<>();
            responseTransaction.put("transactions", transactionListResponse);
            return new ResponseEntity<Object>(responseTransaction, headers, HttpStatus.OK);
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/send-transaction-closing")
    public ResponseEntity<Object> sendTransactionVerificationClosing(HttpServletRequest request, @RequestBody CloserTransactionVerificationClosingRequestDto requestTransactionClosing){
        try {
            boolean responseValidation = closerUserService.sendTransactionVerificationClosing(requestTransactionClosing.getCloserEmail(), requestTransactionClosing.getTransactionId());
            if (responseValidation) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Email Sent for Transaction Closing");
                return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Transaction Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transaction-closing")
    public ResponseEntity<Object> confirmTransactionClosing(HttpServletRequest request, @RequestParam String transactionId){
        try {
            boolean responseValidation = closerUserService.transactionClosing(transactionId);
            if (responseValidation) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Transaction Closed");
                return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Transaction Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transaction-text")
    public ResponseEntity<Object> getTransactionText(HttpServletRequest request, @RequestParam String transactionId) {
        try {
            ResponseTextTransaction responseTextTransaction = closerUserService.responseDataForCloser(transactionId);
            HttpHeaders headers = new HttpHeaders();
            headers.add(statusHeaderName, "Transactions Found For Realtor User");
            return new ResponseEntity<Object>(responseTextTransaction, headers, HttpStatus.OK);
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private ResponseEntity<Object> throwError(String headerValue, HttpStatus httpStatus){
        HttpHeaders headers = new HttpHeaders();
        headers.add(statusHeaderName, headerValue);
        return new ResponseEntity<>(null, headers, httpStatus);
    }
}
