package app.service;

import app.model.Seat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CinemaServiceTest {

	@Test
	void defaultAllocationStartsFurthestRow() {
		CinemaService s = new CinemaServiceImpl("Inception", 8, 10);
		List<Seat> seats = s.proposeDefaultSeats(3);
		assertEquals('A', seats.get(0).rowLabel());
		assertEquals('A', seats.get(1).rowLabel());
		assertEquals('A', seats.get(2).rowLabel());
	}

	@Test
	void confirmAndCheckBooking() {
		CinemaService s = new CinemaServiceImpl("Inception", 2, 4);
		List<Seat> seats = s.proposeDefaultSeats(2);
		String id = s.previewBooking(seats);
		s.confirmBooking(id);
		assertEquals(2, s.getSeatsForBooking(id).size());
		assertEquals(6, s.getAvailableSeatCount());
	}

	@Test
	void customFromPositionFillsRightThenOverflow() {
		CinemaService s = new CinemaServiceImpl("X", 3, 4);
		String id = s.previewBooking(s.proposeDefaultSeats(1));
		List<Seat> custom = s.proposeFromPosition(id, "B03", 4);
		assertNotNull(custom);
		// Should include B3, B4 then overflow to next row (C) using default order (starts
		// mid-left)
		assertEquals("B03", custom.get(0).toString());
		assertEquals("B04", custom.get(1).toString());
	}

}
