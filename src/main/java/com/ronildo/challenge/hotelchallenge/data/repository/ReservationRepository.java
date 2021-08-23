package com.ronildo.challenge.hotelchallenge.data.repository;

import com.ronildo.challenge.hotelchallenge.data.entity.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            " WHERE r.initialDate <= :finalDateParam " +
            " and :initialDateParam <= r.finalDate " +
            " and (:exceptId is null or r.id <> :exceptId) ")
    List<Reservation> checkReservationInPeriod(LocalDate initialDateParam, LocalDate finalDateParam, Long exceptId);
}
