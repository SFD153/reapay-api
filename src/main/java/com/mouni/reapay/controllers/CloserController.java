package com.mouni.reapay.controllers;

import com.mouni.reapay.models.requests.ListAddBrokersRequestDto;
import com.mouni.reapay.models.requests.ListAddClosersRequestDto;
import com.mouni.reapay.models.responses.GetBrokersResponseDto;
import com.mouni.reapay.models.responses.GetClosersResponseDto;
import com.mouni.reapay.services.BrokerService;
import com.mouni.reapay.services.CloserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/v1/closers")
public class CloserController {

    private final CloserService closerService;

    private static final String statusHeaderName = "Status";

    @Autowired
    public CloserController(CloserService closerService){
        this.closerService = closerService;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllBrokers(HttpServletRequest request) {
        try {
            List<GetClosersResponseDto> responseBrokers = closerService.getAllClosers();
            if(responseBrokers != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(statusHeaderName, "Closers list");
                return new ResponseEntity<>(responseBrokers, headers, HttpStatus.OK);
            } else {
                return throwError("Brokers not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return throwError("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Object> getAllBrokers(HttpServletRequest request, @RequestBody ListAddClosersRequestDto closersRequest) {
        try {
            closerService.saveClosers(closersRequest.getClosersList());
            HttpHeaders headers = new HttpHeaders();
            headers.add(statusHeaderName, "Closers Saved");
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
