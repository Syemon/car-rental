package com.syemon.car_rental;

import jakarta.validation.Valid;
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
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    public ReservationController(ReservationService reservationService, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservationRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Unknown principal: " + userId));

        Reservation reservation = reservationService.createReservation(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }
}
