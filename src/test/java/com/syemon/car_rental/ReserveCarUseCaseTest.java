package com.syemon.car_rental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ReserveCarUseCaseTest {

    @Mock CarRepository carRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock UserRepository userRepository;

    @InjectMocks
    ReserveCarUseCase reserveCarUseCase;

    private static final List<ReservationStatus> BLOCKING_STATUSES =
            List.of(ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE);

    @Test
    void shouldCreateReservationWithNullCarAndConfirmedStatus_whenCapacityIsAvailable() {
        User user = user();
        ReservationRequest request = request();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(carRepository.hasAvailableCapacity(
                eq(request.requestedType()),
                eq(request.startTime()),
                eq(request.expectedEndTime()),
                eq(BLOCKING_STATUSES)
        )).thenReturn(true);
        
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(UUID.randomUUID()); // Simulate DB assigning an ID
            return r;
        });

        ReservationResponse result = reserveCarUseCase.createReservation(user.getId(), request);

        assertThat(result.id()).isNotNull();
        assertThat(result.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.carType()).isEqualTo(CarType.SEDAN);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void shouldThrowNoCarAvailableException_whenCapacityIsNotAvailable() {
        User user = user();
        ReservationRequest request = request();

        // Now we need to stub the user first since it happens first in the use case!
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(carRepository.hasAvailableCapacity(any(), any(), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> reserveCarUseCase.createReservation(user.getId(), request))
                .isInstanceOf(NoCarAvailableException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ReservationRequest request = request();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reserveCarUseCase.createReservation(userId, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Unknown user");

        verify(carRepository, never()).hasAvailableCapacity(any(), any(), any(), any());
        verify(reservationRepository, never()).save(any());
    }

    private User user() {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setName("Alice");
        u.setEmail("alice@example.com");
        return u;
    }

    private ReservationRequest request() {
        return new ReservationRequest(
                CarType.SEDAN,
                Instant.now().plusSeconds(3_600),
                Instant.now().plusSeconds(7_200)
        );
    }
}
