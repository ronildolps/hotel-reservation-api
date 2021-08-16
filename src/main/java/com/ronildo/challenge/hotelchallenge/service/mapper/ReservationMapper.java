package com.ronildo.challenge.hotelchallenge.service.mapper;

import com.ronildo.challenge.hotelchallenge.data.entity.Reservation;
import com.ronildo.challenge.hotelchallenge.service.dto.ReservationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;

@Mapper
public interface ReservationMapper {

    Reservation mapReservationDTOToReservation(ReservationDTO reservationDTO);

    ReservationDTO mapReservationToReservationDTO(Reservation reservation);

    Collection<Reservation> mapReservationDTOToReservation(Collection<ReservationDTO> setReservationDTO);

    Collection<ReservationDTO> mapReservationToReservationDTO(Collection<Reservation> setReservation);

    @Mapping(target = "id", ignore = true)
    void updateReservationFromDTO(ReservationDTO reservationDTO, @MappingTarget Reservation reservation);
}

