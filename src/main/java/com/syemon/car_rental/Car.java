package com.syemon.car_rental;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CarType type;

    @Column(unique = true)
    private String licensePlate;

    @Version
    private int version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CarType getType() { return type; }
    public void setType(CarType type) { this.type = type; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public int getVersion() { return version; }
}
