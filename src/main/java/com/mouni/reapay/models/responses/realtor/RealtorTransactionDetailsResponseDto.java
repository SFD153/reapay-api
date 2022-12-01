package com.mouni.reapay.models.responses.realtor;

import com.mouni.reapay.models.RepresentingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RealtorTransactionDetailsResponseDto {

    private String mls;

    private String propertyAddress;

    private String closingDate;

    private String finalPrice;

    private RepresentingType realtorIsRepresenting;
}
