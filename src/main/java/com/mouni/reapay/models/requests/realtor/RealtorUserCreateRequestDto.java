package com.mouni.reapay.models.requests.realtor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealtorUserCreateRequestDto {

    @NotNull(message = "userName field is required")
    private String userName;

    @NotNull(message = "password field is required")
    private String password;

    @NotNull(message = "email field is required")
    private String email;

    @NotNull(message = "phoneNumber field is required")
    private String phoneNumber;

    @NotNull(message = "nmls field is required")
    private String nmls;

    @NotNull(message = "brokerName field is required")
    private String brokerName;

}
