package com.syemon.car_rental;

import org.springframework.data.domain.PageRequest;
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
        List<Car> available = carRepository.findAvailableCarsForUpdate(
                request.requestedType(),
                request.startTime(),
                request.expectedEndTime(),
                BLOCKING_STATUSES,
                PageRequest.of(0, 1)
        );

        if (available.isEmpty()) {
            throw new NoCarAvailableException(
                    "No " + request.requestedType() + " available for the requested time range");
        }

        Car car = available.getFirst();

        User managedUser = userRepository.findById(userId).get();

        Reservation reservation = new Reservation();
        reservation.setUser(managedUser);
        reservation.setRequestedType(request.requestedType());
        reservation.setAssignedCar(car);
        reservation.setStartTime(request.startTime());
        reservation.setExpectedEndTime(request.expectedEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation saved = reservationRepository.save(reservation);

        return saved;
    }
}
