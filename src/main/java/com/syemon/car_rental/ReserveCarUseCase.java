package com.syemon.car_rental;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReserveCarUseCase {

    private static final List<ReservationStatus> BLOCKING_STATUSES =
            List.of(ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE);

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

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

        Reservation reservation = createReservation(request, user);

        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }

    private static Reservation createReservation(ReservationRequest request, User user) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRequestedType(request.requestedType());
        reservation.setAssignedCar(null); // Assigned later
        reservation.setStartTime(request.startTime());
        reservation.setExpectedEndTime(request.expectedEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservation;
    }
}
