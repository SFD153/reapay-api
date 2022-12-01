package com.mouni.reapay.models.requests.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerUserCreateRequestDto {

    @NotNull(message = "userName field is required")
    private String userName;

    @NotNull(message = "password field is required")
    private String password;

    @NotNull(message = "email field is required")
    private String email;

    @NotNull(message = "nrds field is required")
    private String nrds;

    @NotNull(message = "brokerCompanyName field is required")
    private String brokerCompanyName;

    @NotNull(message = "phoneNumber field is required")
    private String phoneNumber;

}
