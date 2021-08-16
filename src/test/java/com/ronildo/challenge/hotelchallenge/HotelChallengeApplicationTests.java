package com.ronildo.challenge.hotelchallenge;

import com.ronildo.challenge.hotelchallenge.controller.ReservationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class HotelChallengeApplicationTests {

	@Autowired
	private ReservationController reservationController;

	@Test
	void contextLoads() {
		assertThat(reservationController).isNotNull();
	}

}
