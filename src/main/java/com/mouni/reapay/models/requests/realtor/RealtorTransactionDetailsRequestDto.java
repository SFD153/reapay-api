package com.mouni.reapay.models.requests.realtor;

import com.mouni.reapay.models.RepresentingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RealtorTransactionDetailsRequestDto {

    private String mls;

    private String propertyAddress;

    private String closingDate;

    private String finalPrice;

    private RepresentingType realtorIsRepresenting;
}
