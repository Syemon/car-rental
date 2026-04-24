package com.syemon.car_rental;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimeRangeValidator.class)
public @interface ValidTimeRange {
    String message() default "expectedEndTime must be after startTime";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
