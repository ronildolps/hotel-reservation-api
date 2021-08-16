package com.ronildo.challenge.hotelchallenge.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = ReservationDatesValidator.class)
@Documented
public @interface ReservationDates {
    String message() default "Invalid reservation dates";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
