package com.syemon.car_rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.assignedCar IS NULL
              AND r.status = 'CONFIRMED'
              AND r.startTime <= :cutoffTime
            """)
    List<Reservation> findUpcomingUnassigned(@Param("cutoffTime") Instant cutoffTime);
}
