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
@Document("broker_users")
@TypeAlias("broker_users")
public class BrokerUser {

    @Id
    @Generated
    private String id;

    private String userName;

    private String password;

    private String email;

    private String nrds;

    private String brokerCompanyName;

    private String phoneNumber;

    private Boolean emailValidation;
}
