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
@Document("realtor_users")
@TypeAlias("realtor_users")
public class RealtorUser {

    @Id
    @Generated
    private String id;

    private String userName;

    private String company;

    private String password;

    private String email;

    private String nmls;

    private String phoneNumber;

    private String brokerName;

    private String managingBroker;

    private String brokerEmail;

    private String brokerPhoneNumber;

    private String closerCompany;

    private String closerName;

    private String closerEmail;

    private String closerPhoneNumber;

    private Boolean emailValidation;

}
