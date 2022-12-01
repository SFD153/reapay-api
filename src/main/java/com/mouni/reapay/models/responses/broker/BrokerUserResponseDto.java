package com.mouni.reapay.models.responses.broker;

import com.mouni.reapay.models.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BrokerUserResponseDto {

    private String userName;

    private String password;

    private String email;

    private String nrds;

    private String brokerCompanyName;

    private String phoneNumber;

    private UserType userType;

    private String errorMessage;
}
