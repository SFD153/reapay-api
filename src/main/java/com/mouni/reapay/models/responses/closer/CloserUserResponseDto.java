package com.mouni.reapay.models.responses.closer;

import com.mouni.reapay.models.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CloserUserResponseDto {

    private String userName;

    private String email;

    private String closerCompanyName;

    private String phoneNumber;

    private UserType userType;

    private String errorMessage;
}
