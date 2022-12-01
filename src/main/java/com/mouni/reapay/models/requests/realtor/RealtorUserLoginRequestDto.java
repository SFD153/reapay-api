package com.mouni.reapay.models.requests.realtor;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealtorUserLoginRequestDto {

    @NotNull(message = "email field is required")
    private String email;

    @NotNull(message = "password field is required")
    private String password;
}
