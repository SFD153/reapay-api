package com.mouni.reapay.models.realtorClosingTransaction;

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
public class RealtorTransactionDetails {

    private String mls;

    private String propertyAddress;

    private Date closingDate;

    private String finalPrice;

    private RepresentingType realtorIsRepresenting;
}
