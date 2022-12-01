package com.mouni.reapay.models.responses.realtor;

import com.mouni.reapay.models.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RealtorUserResponseDto {

    private String userName;

    private String email;

    private String company;

    private String phoneNumber;

    private String brokerName;

    private Integer noOrderBroker;

    private String nmls;

    private String managingBroker;

    private String brokerEmail;

    private String brokerPhoneNumber;

    private String closerCompany;

    private Integer noOrderCloser;

    private String closerName;

    private String closerEmail;

    private String closerPhoneNumber;

    private Boolean emailValidation;

    private UserType userType;

    private String errorMessage;
}
