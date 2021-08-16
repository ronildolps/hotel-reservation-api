package com.ronildo.challenge.hotelchallenge.controller;

import com.ronildo.challenge.hotelchallenge.controller.validation.ReservationDates;
import com.ronildo.challenge.hotelchallenge.service.IReservationService;
import com.ronildo.challenge.hotelchallenge.service.dto.ReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> allReservations() {
        List<ReservationDTO> reservations = reservationService.listAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOneReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findOneById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> saveReservation(
            @Valid @ReservationDates @RequestBody ReservationDTO newReservation) {
        ReservationDTO reservation = reservationService.createReservation(newReservation);

        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/{id}")
                .buildAndExpand(reservation.getId())
                .toUri();

        return ResponseEntity.created(uri).body(reservation);
    }

    @GetMapping("/check_availability")
    public ResponseEntity<?> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate initialDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finalDate) {
        return ResponseEntity.ok(reservationService.checkAvailability(initialDate, finalDate, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long id,
            @Valid @ReservationDates @RequestBody ReservationDTO reservation) {

        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

}
