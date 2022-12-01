package com.mouni.reapay.controllers;

import com.mouni.reapay.models.requests.broker.BrokerUserCreateRequestDto;
import com.mouni.reapay.models.responses.broker.BrokerUserResponseDto;
import com.mouni.reapay.services.AwsService;
import com.mouni.reapay.services.BrokerService;
import com.mouni.reapay.services.MediaUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@RequestMapping("/v1/pdf")
public class PdfController {

    private final MediaUtilsService mediaUtilsService;

    @Autowired
    public PdfController(MediaUtilsService mediaUtilsService) {
        this.mediaUtilsService = mediaUtilsService;
    }

    @PostMapping("/save")
    public ResponseEntity<Object> createBrokerUser(HttpServletRequest request, @RequestParam String transactionId) throws ParseException {
        mediaUtilsService.createPdf(transactionId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Status", "Saved");
        return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
    }
}
