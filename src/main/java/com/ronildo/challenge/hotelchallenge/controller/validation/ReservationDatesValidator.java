package com.ronildo.challenge.hotelchallenge.controller.validation;

import com.ronildo.challenge.hotelchallenge.service.dto.ReservationDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class ReservationDatesValidator implements ConstraintValidator<ReservationDates, ReservationDTO> {
    @Override
    public boolean isValid(ReservationDTO reservationDTO,
                           ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean isValid = true;

        if(DAYS.between(reservationDTO.getInitialDate(), reservationDTO.getFinalDate()) > 3) {
            context
                    .buildConstraintViolationWithTemplate("The stay can’t be longer than 3 days")
                    .addConstraintViolation();
            isValid = false;
        }

        if(DAYS.between(LocalDate.now(), reservationDTO.getFinalDate()) > 30) {
            context
                    .buildConstraintViolationWithTemplate("The stay can’t be reserved more than 30 days in advance")
                    .addConstraintViolation();
            isValid = false;
        }

        if(reservationDTO.getFinalDate().isEqual(reservationDTO.getInitialDate())  ||
                reservationDTO.getFinalDate().isBefore(reservationDTO.getInitialDate())) {
            context
                    .buildConstraintViolationWithTemplate("The final date must be after the initial date")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void initialize(ReservationDates constraintAnnotation) {
    }
}
