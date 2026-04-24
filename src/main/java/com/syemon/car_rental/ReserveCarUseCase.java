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
    public ReservationResponse createReservation(UUID userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Unknown user: " + userId));

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

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRequestedType(request.requestedType());
        reservation.setAssignedCar(null); // Assigned later
        reservation.setStartTime(request.startTime());
        reservation.setExpectedEndTime(request.expectedEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }
}
