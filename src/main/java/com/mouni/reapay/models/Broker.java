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
@Document("brokers")
@TypeAlias("brokers")
public class Broker {

    @Id
    @Generated
    private String id;

    private Integer noOrder;

    private String brokerName;

    private String brokerAddress;
}
