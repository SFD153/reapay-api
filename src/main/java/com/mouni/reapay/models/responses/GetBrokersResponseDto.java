package com.mouni.reapay.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class GetBrokersResponseDto {

    private Integer noOrder;

    private String brokerName;

    private String brokerAddress;
}
