package com.syemon.car_rental;

import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        CarType carType,
        Instant startTime,
        Instant endTime,
        ReservationStatus status
) {
    public static ReservationResponse fromEntity(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRequestedType(),
                reservation.getStartTime(),
                reservation.getExpectedEndTime(),
                reservation.getStatus()
        );
    }
}
