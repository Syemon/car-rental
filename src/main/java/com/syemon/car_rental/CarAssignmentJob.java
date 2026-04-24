package com.syemon.car_rental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CarAssignmentJob {

    private static final Logger log = LoggerFactory.getLogger(CarAssignmentJob.class);

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    public CarAssignmentJob(ReservationRepository reservationRepository, CarRepository carRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
    }

    // Runs every hour, checking for reservations starting within 24 hours
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void assignCarsToUpcomingReservations() {
        Instant tomorrow = Instant.now().plus(24, ChronoUnit.HOURS);
        List<Reservation> upcoming = reservationRepository.findUpcomingUnassigned(tomorrow);

        for (Reservation reservation : upcoming) {
            List<Car> availableCars = carRepository.findAvailableCarsForUpdate(
                    reservation.getRequestedType(),
                    reservation.getStartTime(),
                    reservation.getExpectedEndTime(),
                    List.of(ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE),
                    PageRequest.of(0, 1)
            );

            if (!availableCars.isEmpty()) {
                Car assignedCar = availableCars.getFirst();
                reservation.setAssignedCar(assignedCar);
                reservationRepository.save(reservation);
                log.info("Assigned car {} to reservation {}", assignedCar.getId(), reservation.getId());
            } else {
                log.error("Failed to assign a car for reservation {}. Possible overbooking or no cars available!", reservation.getId());
                // Handle edge case here (e.g. notify admin)
            }
        }
    }
}
