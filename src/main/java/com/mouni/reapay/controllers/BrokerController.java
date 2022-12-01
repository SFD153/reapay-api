package com.mouni.reapay.controllers;

import com.mouni.reapay.models.requests.ListAddBrokersRequestDto;
import com.mouni.reapay.models.responses.GetBrokersResponseDto;
import com.mouni.reapay.services.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/v1/brokers")
public class BrokerController {


    private final BrokerService brokerService;

    private static final String statusHeaderName = "Status";

    @Autowired
    public BrokerController(BrokerService brokerService){
        this.brokerService = brokerService;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllBrokers(HttpServletRequest request) {
        try {
            List<GetBrokersResponseDto> responseBrokers = brokerService.getAllBrokers();
            if(responseBrokers != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Brokers list");
                return new ResponseEntity<>(responseBrokers, headers, HttpStatus.OK);
            } else {
                return throwError("Brokers not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Object> getAllBrokers(HttpServletRequest request, @RequestBody ListAddBrokersRequestDto brokersRequest) {
        try {
            brokerService.saveBrokers(brokersRequest.getBrokersList());
            HttpHeaders headers = new HttpHeaders();
            headers.add(statusHeaderName, "Brokers Saved");
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
            }
         catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Object> throwError(String headerValue, HttpStatus httpStatus){
        HttpHeaders headers = new HttpHeaders();
        headers.add(statusHeaderName, headerValue);
        return new ResponseEntity<>(null, headers, httpStatus);
    }
}
