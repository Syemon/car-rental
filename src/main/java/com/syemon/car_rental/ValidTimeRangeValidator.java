package com.syemon.car_rental;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTimeRangeValidator implements ConstraintValidator<ValidTimeRange, ReservationRequest> {

    @Override
    public boolean isValid(ReservationRequest value, ConstraintValidatorContext context) {
        if (value.startTime() == null || value.expectedEndTime() == null) {
            return true;
        }
        return value.expectedEndTime().isAfter(value.startTime());
    }
}
