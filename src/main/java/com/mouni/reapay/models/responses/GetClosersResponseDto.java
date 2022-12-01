package com.mouni.reapay.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class GetClosersResponseDto {

    private Integer noOrder;

    private String closerName;

    private String closerAddress;
}
