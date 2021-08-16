package com.ronildo.challenge.hotelchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronildo.challenge.hotelchallenge.exception.DatesNotAvailableForReservationException;
import com.ronildo.challenge.hotelchallenge.exception.ReservationNotFoundException;
import com.ronildo.challenge.hotelchallenge.service.IReservationService;
import com.ronildo.challenge.hotelchallenge.service.dto.ReservationDTO;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ReservationController.class)
class ReservationControllerTest {

    private final String apiController = "/api/reservations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IReservationService reservationService;

    @Test
    void listAllReservations() throws Exception {
        List<ReservationDTO> listReturn = new ArrayList<>();
        listReturn.add(new ReservationDTO(1L, LocalDate.now(), LocalDate.now()));

        when(reservationService.listAllReservations())
                .thenReturn(listReturn);

        MvcResult mvcResult = mockMvc
                .perform(get(apiController)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedResponseBody = objectMapper.writeValueAsString(listReturn);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void retrieveOneReservationFound() throws Exception {
        ReservationDTO reservation  = new ReservationDTO(1L, LocalDate.now(), LocalDate.now());

        when(reservationService.findOneById(reservation.getId()))
                .thenReturn(reservation);

        MvcResult mvcResult = mockMvc
                .perform(get(apiController + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedResponseBody = objectMapper.writeValueAsString(reservation);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void retrieveOneReservationNotFound() throws Exception {
        when(reservationService.findOneById(1L))
                .thenThrow(ReservationNotFoundException.class);

        mockMvc
                .perform(get(apiController + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationNotFoundException));
    }

    @Test
    void saveReservationInvalidPeriodLongerThan3Days() throws Exception {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setInitialDate(LocalDate.now().plusDays(1));
        reservation.setFinalDate(LocalDate.now().plusDays(5));

        mockMvc
                .perform(post(apiController)
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details",
                        StringContains.containsString("The stay can’t be longer than 3 days")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void saveReservationInvalidMoreThan30DaysBefore() throws Exception {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setInitialDate(LocalDate.now().plusDays(30));
        reservation.setFinalDate(LocalDate.now().plusDays(31));

        mockMvc
                .perform(post(apiController)
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details",
                        StringContains.containsString("The stay can’t be reserved more than 30 days in advance")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void saveReservationInvalidFinalDateBeforeFinalDate() throws Exception {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setInitialDate(LocalDate.now().plusDays(3));
        reservation.setFinalDate(LocalDate.now().plusDays(3));

        mockMvc
                .perform(post(apiController)
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details",
                        StringContains.containsString("The final date must be after the initial date")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void saveReservation_Invalid_InitialDateNull() throws Exception {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setFinalDate(LocalDate.now().plusDays(2));

        mockMvc
                .perform(post(apiController)
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details",
                        StringContains.containsString("initialDate is mandatory;")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void saveReservationInvalidFinalDateNull() throws Exception {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setInitialDate(LocalDate.now());

        mockMvc
                .perform(post(apiController)
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details",
                        StringContains.containsString("finalDate is mandatory;")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void saveReservationInvalidNotAvailable() throws Exception {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setInitialDate(LocalDate.now().plusDays(1));
        reservation.setFinalDate(reservation.getInitialDate().plusDays(2));

        when(reservationService.createReservation(any()))
                .thenThrow(DatesNotAvailableForReservationException.class);

        mockMvc
                .perform(post(apiController)
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DatesNotAvailableForReservationException));
    }

    @Test
    void saveReservationSuccess() throws Exception {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setInitialDate(LocalDate.now().plusDays(1));
        reservation.setFinalDate(reservation.getInitialDate().plusDays(2));

        when(reservationService.createReservation(any(ReservationDTO.class)))
                .thenReturn(reservation);

        MvcResult mvcResult = mockMvc
                .perform(post(apiController)
                    .content(objectMapper.writeValueAsString(reservation))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String expectedResponseBody = objectMapper.writeValueAsString(reservation);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void checkAvailabilityAvailable() throws Exception {
        when(reservationService.checkAvailability(any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(true);

        MvcResult mvcResult = mockMvc
                .perform(get(apiController + "/check_availability")
                        .queryParam("initialDate", LocalDate.now().toString())
                        .queryParam("finalDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andReturn();

        String expectedResponseBody = objectMapper.writeValueAsString(true);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void checkAvailabilityNotAvailable() throws Exception {
        when(reservationService.checkAvailability(any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(false);

        MvcResult mvcResult = mockMvc
                .perform(get(apiController + "/check_availability")
                        .queryParam("initialDate", LocalDate.now().toString())
                        .queryParam("finalDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andReturn();

        String expectedResponseBody = objectMapper.writeValueAsString(false);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void updateReservationSuccess() throws Exception {
        ReservationDTO reservationInput = new ReservationDTO();
        reservationInput.setInitialDate(LocalDate.now().plusDays(1));
        reservationInput.setFinalDate(reservationInput.getInitialDate().plusDays(2));

        ReservationDTO reservationOutput = new ReservationDTO();
        reservationOutput.setId(5L);
        reservationOutput.setInitialDate(reservationInput.getInitialDate());
        reservationOutput.setFinalDate(reservationInput.getFinalDate());

        when(reservationService.updateReservation(any(Long.class), any(ReservationDTO.class)))
                .thenReturn(reservationOutput);

        MvcResult mvcResult = mockMvc
                .perform(put(apiController+"/"+reservationOutput.getId())
                        .content(objectMapper.writeValueAsString(reservationInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedResponseBody = objectMapper.writeValueAsString(reservationOutput);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void updateReservationDatesNotAvailable() throws Exception {
        ReservationDTO reservationInput = new ReservationDTO();
        reservationInput.setInitialDate(LocalDate.now().plusDays(1));
        reservationInput.setFinalDate(reservationInput.getInitialDate().plusDays(2));

        when(reservationService.updateReservation(any(Long.class), any(ReservationDTO.class)))
                .thenThrow(DatesNotAvailableForReservationException.class);

        mockMvc
                .perform(put(apiController+"/5")
                        .content(objectMapper.writeValueAsString(reservationInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DatesNotAvailableForReservationException));
    }

    @Test
    void updateReservationDatesNotValid() throws Exception {
        ReservationDTO reservationInput = new ReservationDTO();
        reservationInput.setInitialDate(LocalDate.now().plusDays(3));
        reservationInput.setFinalDate(LocalDate.now().plusDays(3));

        mockMvc
                .perform(put(apiController+"/5")
                        .content(objectMapper.writeValueAsString(reservationInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details",
                        StringContains.containsString("The final date must be after the initial date")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void updateReservationNotFound() throws Exception {
        ReservationDTO reservationInput = new ReservationDTO();
        reservationInput.setInitialDate(LocalDate.now().plusDays(1));
        reservationInput.setFinalDate(reservationInput.getInitialDate().plusDays(2));

        when(reservationService.updateReservation(any(Long.class), any(ReservationDTO.class)))
                .thenThrow(ReservationNotFoundException.class);

        mockMvc
                .perform(put(apiController+"/5")
                        .content(objectMapper.writeValueAsString(reservationInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationNotFoundException));
    }

    @Test
    void deleteReservationSuccess() throws Exception {
        mockMvc
                .perform(delete(apiController+"/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReservationNotFound() throws Exception {
        doThrow(ReservationNotFoundException.class).when(reservationService).deleteReservation(anyLong());

        mockMvc
                .perform(delete(apiController+"/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationNotFoundException));
    }
}