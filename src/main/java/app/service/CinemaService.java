package app.service;

import app.model.Seat;

import java.util.List;
import java.util.Set;

public interface CinemaService {

	String getTitle();

	int getRows();

	int getCols();

	int getAvailableSeatCount();

	List<Seat> proposeDefaultSeats(int count);

	String previewBooking(List<Seat> seats);

	void confirmBooking(String bookingId);

	List<Seat> proposeFromPosition(String bookingId, String position, int count);

	List<Seat> getSeatsForBooking(String bookingId);

	Set<Seat> getAllBookedSeatsExcept(String bookingId);

}
