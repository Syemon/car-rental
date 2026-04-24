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
    public ResponseEntity<ReservationResponse> createReservation(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservationRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reserveCarUseCase.createReservation(userId, request));
    }
}
