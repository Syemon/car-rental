package com.syemon.car_rental;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReserveCarUseCase reserveCarUseCase;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservationRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Unknown user: " + userId));

        Reservation reservation = reserveCarUseCase.createReservation(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }
}
