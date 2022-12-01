package com.mouni.reapay.models.requests.closer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloserUserLoginRequestDto {
    @NotNull(message = "email field is required")
    private String email;

    @NotNull(message = "password field is required")
    private String password;
}
