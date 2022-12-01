package com.mouni.reapay.models.requests.closer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTextTransaction {
    private String transactionId;

    private String closerCompanyName;

    private String closerCompanyAddress;

    private String closerName;

    private String closerEmail;

    private String closingDate;

    private String closerPhone;

    private String realtorName;

    private String brokerCompanyName;

    private String mls;

    private String propertyAddress;

    private String firstParagraph;

    private String secondParagraph;

    private String thirdParagraph;

    private String fourthParagraph;

    private String fifthParagraph;
}
