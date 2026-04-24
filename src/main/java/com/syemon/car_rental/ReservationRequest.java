package com.syemon.car_rental;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@ValidTimeRange
public record ReservationRequest(
    @NotNull CarType requestedType,
    @NotNull @Future Instant startTime,
    @NotNull @Future Instant expectedEndTime
) {}
