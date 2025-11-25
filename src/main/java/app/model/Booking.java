package app.model;

import java.util.ArrayList;
import java.util.List;

public class Booking {

	public final String id;

	public final List<Seat> seats;

	public Booking(String id) {
		this.id = id;
		this.seats = new ArrayList<>();
	}

}
