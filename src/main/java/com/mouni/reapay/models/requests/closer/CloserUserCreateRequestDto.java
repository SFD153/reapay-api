package com.mouni.reapay.models.requests.closer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloserUserCreateRequestDto {

    @NotNull(message = "userName field is required")
    private String userName;

    @NotNull(message = "password field is required")
    private String password;

    @NotNull(message = "email field is required")
    private String email;

    @NotNull(message = "closerCompanyName field is required")
    private String closerCompanyName;

    @NotNull(message = "phoneNumber field is required")
    private String phoneNumber;
}
