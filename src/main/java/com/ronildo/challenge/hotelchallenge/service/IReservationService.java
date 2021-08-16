package com.ronildo.challenge.hotelchallenge.service;

import com.ronildo.challenge.hotelchallenge.service.dto.ReservationDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReservationService {

    ReservationDTO createReservation(ReservationDTO newReservation);

    List<ReservationDTO> listAllReservations();

    ReservationDTO findOneById(Long id);

    Boolean checkAvailability(LocalDate initialDate, LocalDate finalDate, Long id);

    ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO);

    void deleteReservation(Long id);
}
