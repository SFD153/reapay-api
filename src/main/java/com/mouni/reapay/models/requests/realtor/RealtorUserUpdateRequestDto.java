package com.mouni.reapay.models.requests.realtor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealtorUserUpdateRequestDto {

    @NotNull(message = "email field is required")
    private String email;

    @NotNull(message = "userName field is required")
    private String userName;

    @NotNull(message = "realtorCompany field is required")
    private String company;

    @NotNull(message = "nmls field is required")
    private String nmls;

    @NotNull(message = "phoneNumber field is required")
    private String phoneNumber;

    @NotNull(message = "brokerName field is required")
    private String brokerName;

    @NotNull(message = "managingBroker field is required")
    private String managingBroker;

    @NotNull(message = "brokerEmail field is required")
    private String brokerEmail;

    @NotNull(message = "brokerPhoneNumber field is required")
    private String brokerPhoneNumber;

    @NotNull(message = "closerCompany field is required")
    private String closerCompany;

    @NotNull(message = "closerName field is required")
    private String closerName;

    @NotNull(message = "closerEmail field is required")
    private String closerEmail;

    @NotNull(message = "closerPhoneNumber field is required")
    private String closerPhoneNumber;
}
