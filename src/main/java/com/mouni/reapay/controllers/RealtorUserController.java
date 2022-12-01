package com.mouni.reapay.controllers;

import com.mouni.reapay.models.requests.realtor.RealtorTransactionRequestDto;
import com.mouni.reapay.models.requests.realtor.RealtorUserCreateRequestDto;
import com.mouni.reapay.models.requests.realtor.RealtorUserLoginRequestDto;
import com.mouni.reapay.models.requests.realtor.RealtorUserUpdateRequestDto;
import com.mouni.reapay.models.responses.realtor.RealtorClosingTransactionResponseDto;
import com.mouni.reapay.models.responses.realtor.RealtorUserResponseDto;
import com.mouni.reapay.services.RealtorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/user/realtor")
public class RealtorUserController {

    private final RealtorUserService realtorUserService;

    private static final String statusHeaderName = "Status";

    @Autowired
    public RealtorUserController(RealtorUserService realtorUserService) {
        this.realtorUserService = realtorUserService;
    }

    @GetMapping("/test")
    public ResponseEntity<Object> test(HttpServletRequest request) {
        realtorUserService.test();
        return new ResponseEntity<Object>(null, null, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Object> getRealtor(HttpServletRequest request, @RequestParam String realtorEmail) {
        try {
            RealtorUserResponseDto responseUser = realtorUserService.getRealtor(realtorEmail);
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
    @PostMapping("/login")
    public ResponseEntity<Object> loginRealtorUser(HttpServletRequest request, @Validated @RequestBody RealtorUserLoginRequestDto realtorUserRequest) {
        try {
            RealtorUserResponseDto responseUser = realtorUserService.checkRealtorUserAndPassword(realtorUserRequest);
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
    public ResponseEntity<Object> createRealtorUser(HttpServletRequest request, @RequestBody RealtorUserCreateRequestDto realtorUserRequest) {
        try {
            RealtorUserResponseDto responseUser = realtorUserService.createRealtorUser(realtorUserRequest);
            if (responseUser != null) {
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
    public ResponseEntity<Object> emailValidation(HttpServletRequest request, @RequestParam String realtorUserId) {
        try {
            boolean responseValidation = realtorUserService.realtorEmailValidation(realtorUserId);
            if (responseValidation) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Email Validated");
                return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Realtor User Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/update-details")
    public ResponseEntity<Object> updateRealtorAccountDetails(HttpServletRequest request, @RequestBody RealtorUserUpdateRequestDto requestUpdateDto) {
        try {
            RealtorUserResponseDto responseUser = realtorUserService.updateRealtorDetails(requestUpdateDto);
            if (responseUser != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "User details updated");
                return new ResponseEntity<Object>(responseUser, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Realtor User", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transaction")
    public ResponseEntity<Object> createTransaction(HttpServletRequest request, @RequestBody RealtorTransactionRequestDto transactionRequestDto) {
        try {
            RealtorClosingTransactionResponseDto transactionResponse = realtorUserService.createTransaction(transactionRequestDto);
            if (transactionResponse != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Transaction Created");
                return new ResponseEntity<Object>(transactionResponse, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Realtor User", HttpStatus.NOT_FOUND);
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
    public ResponseEntity<Object> getAllTransaction(HttpServletRequest request, @RequestParam String realtorEmail) {
        try {
            List<RealtorClosingTransactionResponseDto> transactionListResponse = realtorUserService.getTransactionsForRealtor(realtorEmail);
            HttpHeaders headers = new HttpHeaders();
            headers.add(statusHeaderName, "Transactions Found For Current User Email");
            HashMap<String, List<RealtorClosingTransactionResponseDto>> responseTransaction = new LinkedHashMap<>();
            responseTransaction.put("transactions", transactionListResponse);
            return new ResponseEntity<Object>(responseTransaction, headers, HttpStatus.OK);
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/confirm-transaction")
    public ResponseEntity<Object> confirmTransaction(HttpServletRequest request, @RequestParam String transactionId) {
        try {
            boolean responseConfirmation = realtorUserService.realtorConfirmTransactionClosing(transactionId);
            if (responseConfirmation) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Transaction Confirmed");
                return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
            } else {
                return throwError("Invalid Transaction Id", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Object> throwError(String headerValue, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(statusHeaderName, headerValue);
        return new ResponseEntity<>(null, headers, httpStatus);
    }
}
