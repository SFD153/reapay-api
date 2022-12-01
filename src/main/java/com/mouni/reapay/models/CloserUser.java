package com.mouni.reapay.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document("closer_users")
@TypeAlias("closer_users")
public class CloserUser {

    @Id
    @Generated
    private String id;

    private String userName;

    private String password;

    private String email;

    private String closerCompanyName;

    private String phoneNumber;

    private Boolean emailValidation;
}
