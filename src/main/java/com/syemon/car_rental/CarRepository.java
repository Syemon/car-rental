package com.syemon.car_rental;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID> {

    @Query("""
            SELECT c FROM Car c
            WHERE c.type = :type
              AND NOT EXISTS (
                SELECT r FROM Reservation r
                WHERE r.assignedCar = c
                  AND r.status IN :activeStatuses
                  AND r.startTime < :requestedEnd
                  AND r.expectedEndTime > :requestedStart
              )
            ORDER BY c.id
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Car> findAvailableCarsForUpdate(
            @Param("type") CarType type,
            @Param("requestedStart") Instant requestedStart,
            @Param("requestedEnd") Instant requestedEnd,
            @Param("activeStatuses") Collection<ReservationStatus> activeStatuses,
            Pageable pageable
    );
}
