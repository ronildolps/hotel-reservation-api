package com.ronildo.challenge.hotelchallenge.service.impl;

import com.ronildo.challenge.hotelchallenge.data.entity.Reservation;
import com.ronildo.challenge.hotelchallenge.data.repository.ReservationRepository;
import com.ronildo.challenge.hotelchallenge.exception.DatesNotAvailableForReservationException;
import com.ronildo.challenge.hotelchallenge.exception.ReservationNotFoundException;
import com.ronildo.challenge.hotelchallenge.service.IReservationService;
import com.ronildo.challenge.hotelchallenge.service.dto.ReservationDTO;
import com.ronildo.challenge.hotelchallenge.service.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationDTO createReservation(ReservationDTO newReservation) {
        if (!this.checkAvailability(newReservation.getInitialDate(), newReservation.getFinalDate(), null)) {
            throw new DatesNotAvailableForReservationException();
        }

        Reservation reservation = reservationMapper.mapReservationDTOToReservation(newReservation);
        reservation = reservationRepository.save(reservation);

        return reservationMapper.mapReservationToReservationDTO(reservation);
    }

    @Override
    public List<ReservationDTO> listAllReservations() {
        Collection<Reservation> all = (Collection<Reservation>) reservationRepository.findAll();

        return (List<ReservationDTO>) reservationMapper.mapReservationToReservationDTO(all);
    }

    @Override
    public ReservationDTO findOneById(Long id) {
        Optional<Reservation> entity = reservationRepository.findById(id);

        return reservationMapper.mapReservationToReservationDTO(
                entity.orElseThrow(() -> new ReservationNotFoundException(id)));
    }

    @Override
    public Boolean checkAvailability(LocalDate initialDate, LocalDate finalDate, Long exceptId) {
        List<Reservation> reservations = reservationRepository.checkReservationInPeriod(initialDate, finalDate, exceptId);
        return reservations.isEmpty();
    }

    @Override
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
        if (!this.checkAvailability(reservationDTO.getInitialDate(), reservationDTO.getFinalDate(), id)) {
            throw new DatesNotAvailableForReservationException();
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        reservationMapper.updateReservationFromDTO(reservationDTO, reservation);
        return reservationMapper.mapReservationToReservationDTO(reservationRepository.save(reservation));
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        reservationRepository.delete(reservation);
    }
}
