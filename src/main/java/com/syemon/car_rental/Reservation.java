package com.syemon.car_rental;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CarType requestedType;

    @ManyToOne
    @JoinColumn(name = "assigned_car_id")
    private Car assignedCar;

    private Instant startTime;
    private Instant expectedEndTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}
