package com.syemon.car_rental;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReserveCarUseCase {

    private static final List<ReservationStatus> BLOCKING_STATUSES =
            List.of(ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE);

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReserveCarUseCase(CarRepository carRepository,
                             ReservationRepository reservationRepository,
                             UserRepository userRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Reservation createReservation(UUID userId, ReservationRequest request) {
        boolean hasCapacity = carRepository.hasAvailableCapacity(
                request.requestedType(),
                request.startTime(),
                request.expectedEndTime(),
                BLOCKING_STATUSES
        );

        if (!hasCapacity) {
            throw new NoCarAvailableException(
                    "No " + request.requestedType() + " available for the requested time range");
        }

        User managedUser = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        Reservation reservation = new Reservation();
        reservation.setUser(managedUser);
        reservation.setRequestedType(request.requestedType());
        reservation.setAssignedCar(null);
        reservation.setStartTime(request.startTime());
        reservation.setExpectedEndTime(request.expectedEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        return reservationRepository.save(reservation);
    }
}
