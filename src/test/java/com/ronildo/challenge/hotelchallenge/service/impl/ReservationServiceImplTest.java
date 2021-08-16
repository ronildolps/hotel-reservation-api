package com.ronildo.challenge.hotelchallenge.service.impl;

import com.ronildo.challenge.hotelchallenge.data.entity.Reservation;
import com.ronildo.challenge.hotelchallenge.data.repository.ReservationRepository;
import com.ronildo.challenge.hotelchallenge.exception.DatesNotAvailableForReservationException;
import com.ronildo.challenge.hotelchallenge.exception.ReservationNotFoundException;
import com.ronildo.challenge.hotelchallenge.service.IReservationService;
import com.ronildo.challenge.hotelchallenge.service.dto.ReservationDTO;
import com.ronildo.challenge.hotelchallenge.service.mapper.ReservationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReservationServiceImplTest {

    @MockBean
    ReservationRepository reservationRepository;

    @Autowired
    IReservationService reservationService;

    @Autowired
    ReservationMapper reservationMapper;

    @Test
    void listAllReservations() {
        ReservationDTO expected1 = new ReservationDTO(1L, LocalDate.now(), LocalDate.now());
        ReservationDTO expected2 = new ReservationDTO(2L, LocalDate.now().plusDays(3), LocalDate.now().plusDays(7));
        ReservationDTO expected3 = new ReservationDTO(3L, LocalDate.now().plusDays(6), LocalDate.now().plusDays(7));
        List<ReservationDTO> expected = Arrays.asList(expected1, expected2, expected3);

        when(reservationRepository.findAll())
                .thenReturn(reservationMapper.mapReservationDTOToReservation(expected));

        List<ReservationDTO> found = reservationService.listAllReservations();

        assertEquals(expected, found);
    }

    @Test
    void findOneByIdFound() {
        ReservationDTO expected = new ReservationDTO(1L, LocalDate.now(), LocalDate.now());

        when(reservationRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(reservationMapper.mapReservationDTOToReservation(expected)));

        ReservationDTO found = reservationService.findOneById(1L);

        assertEquals(expected, found);
    }

    @Test
    void findOneByIdNotFound() {
        when(reservationRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(ReservationNotFoundException.class, () -> reservationService.findOneById(1L));

        assertEquals("Could not find reservation 1", exception.getMessage());
    }

    @Test
    void checkAvailability() {
        when(reservationRepository.checkReservationInPeriod(any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(new ArrayList<>());

        Boolean found = reservationService.checkAvailability(LocalDate.now(), LocalDate.now(), null);

        assertEquals(Boolean.TRUE, found);
    }

    @Test
    void checkAvailabilityNotAvailable() {
        when(reservationRepository.checkReservationInPeriod(any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(Collections.singletonList(new Reservation()));

        Boolean found = reservationService.checkAvailability(LocalDate.now(), LocalDate.now(), null);

        assertEquals(Boolean.FALSE, found);
    }

    @Test
    void createReservationSuccess() {
        ReservationDTO input = new ReservationDTO(null, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
        when(reservationRepository.save(any()))
                .thenAnswer(i -> {
                    ((Reservation)i.getArgument(0)).setId(5L);
                    return i.getArgument(0);
                });

        ReservationDTO found = reservationService.createReservation(input);

        assertNotNull(found.getId());
        assertEquals(input.getInitialDate(), found.getInitialDate());
        assertEquals(input.getFinalDate(), found.getFinalDate());
    }

    @Test
    void createReservationNotAvailableDates() {
        ReservationDTO input = new ReservationDTO(null, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));

        when(reservationRepository.checkReservationInPeriod(any(), any(), any()))
                .thenReturn(Collections.singletonList(new Reservation()));

        Throwable exception = assertThrows(DatesNotAvailableForReservationException.class,
                () -> reservationService.createReservation(input));

        assertEquals("Dates are not available for reservation", exception.getMessage());
    }

    @Test
    void updateReservationSuccess() {
        ReservationDTO input = new ReservationDTO(4L, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
        Reservation entityDB = new Reservation(4L, LocalDate.now().plusDays(3), LocalDate.now().plusDays(5));

        when(reservationRepository.findById(4L))
                .thenReturn(Optional.of(entityDB));
        when(reservationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0) );

        ReservationDTO found = reservationService.updateReservation(input.getId(), input);

        assertEquals(input.getId(), found.getId());
        assertEquals(input.getInitialDate(), found.getInitialDate());
        assertEquals(input.getFinalDate(), found.getFinalDate());
    }

    @Test
    void updateReservationDatesNotAvailable() {
        ReservationDTO input = new ReservationDTO(4L, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));

        when(reservationRepository.checkReservationInPeriod(any(), any(), any()))
                .thenReturn(Collections.singletonList(new Reservation()));

        Throwable exception = assertThrows(DatesNotAvailableForReservationException.class,
                () -> reservationService.updateReservation(input.getId(), input));

        assertEquals("Dates are not available for reservation", exception.getMessage());
    }

    @Test
    void updateReservationNotFound() {
        ReservationDTO input = new ReservationDTO(4L, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));

        when(reservationRepository.checkReservationInPeriod(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.findById(any()))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(ReservationNotFoundException.class,
                () -> reservationService.updateReservation(input.getId(), input));

        assertEquals("Could not find reservation " + input.getId(), exception.getMessage());
    }

    @Test
    void deleteReservationSuccess() {
        Reservation entityDB = new Reservation(4L, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));

        when(reservationRepository.findById(4L))
                .thenReturn(Optional.of(entityDB));

        reservationService.deleteReservation(entityDB.getId());
    }

    @Test
    void deleteReservationNotFound() {
        when(reservationRepository.findById(4L))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(ReservationNotFoundException.class,
                () -> reservationService.deleteReservation(4L));

        assertEquals("Could not find reservation 4", exception.getMessage());
    }
}