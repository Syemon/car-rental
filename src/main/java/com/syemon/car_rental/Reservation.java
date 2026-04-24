package com.syemon.car_rental;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CarType requestedType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_car_id")
    private Car assignedCar;

    private Instant startTime;
    private Instant expectedEndTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CarType getRequestedType() { return requestedType; }
    public void setRequestedType(CarType requestedType) { this.requestedType = requestedType; }
    public Car getAssignedCar() { return assignedCar; }
    public void setAssignedCar(Car assignedCar) { this.assignedCar = assignedCar; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getExpectedEndTime() { return expectedEndTime; }
    public void setExpectedEndTime(Instant expectedEndTime) { this.expectedEndTime = expectedEndTime; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
