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
public class AddCloserRequestDto {

    @NotNull(message = "noOrder field is required")
    private Integer noOrder;

    @NotNull(message = "closerName field is required")
    private String closerName;

    @NotNull(message = "closerAddress field is required")
    private String closerAddress;

}
