package com.ronildo.challenge.hotelchallenge.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDTO {
    Long id;

    @NotNull(message = "initialDate is mandatory")
    @Future(message = "initialDate must be in the future")
    LocalDate initialDate;

    @NotNull(message = "finalDate is mandatory")
    @Future(message = "finalDate must be in the future")
    LocalDate finalDate;
}

