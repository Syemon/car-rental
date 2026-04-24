package com.syemon.car_rental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock CarRepository carRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock UserRepository userRepository;

    @InjectMocks ReservationService reservationService;

    private static final List<ReservationStatus> BLOCKING_STATUSES =
            List.of(ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE);

    @Test
    void happyPath_reservationSavedWithAssignedCarAndConfirmedStatus() {
        User user = user();
        Car car = car();
        ReservationRequest request = request();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(carRepository.findAvailableCarsForUpdate(
                eq(request.requestedType()),
                eq(request.startTime()),
                eq(request.expectedEndTime()),
                eq(BLOCKING_STATUSES),
                eq(PageRequest.of(0, 1))
        )).thenReturn(List.of(car));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.createReservation(user, request);

        assertThat(result.getAssignedCar()).isEqualTo(car);
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.getRequestedType()).isEqualTo(CarType.SEDAN);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void noAvailability_throwsNoCarAvailableException_saveNeverCalled() {
        User user = user();
        ReservationRequest request = request();

        when(carRepository.findAvailableCarsForUpdate(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> reservationService.createReservation(user, request))
                .isInstanceOf(NoCarAvailableException.class);

        verify(reservationRepository, never()).save(any());
    }

    private User user() {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setName("Alice");
        u.setEmail("alice@example.com");
        return u;
    }

    private Car car() {
        Car c = new Car();
        c.setId(UUID.randomUUID());
        c.setType(CarType.SEDAN);
        c.setLicensePlate("SED-001");
        return c;
    }

    private ReservationRequest request() {
        return new ReservationRequest(
                CarType.SEDAN,
                Instant.now().plusSeconds(3_600),
                Instant.now().plusSeconds(7_200)
        );
    }
}
