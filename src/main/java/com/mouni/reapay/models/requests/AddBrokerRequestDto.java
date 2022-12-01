package com.mouni.reapay.models.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AddBrokerRequestDto {

    @NotNull(message = "noOrder field is required")
    private Integer noOrder;

    @NotNull(message = "brokerName field is required")
    private String brokerName;

    @NotNull(message = "address field is required")
    private String address;
}
