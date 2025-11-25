package app;

import app.model.Seat;
import app.service.CinemaService;
import app.service.CinemaServiceImpl;
import app.util.SeatMapPrinter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class GicCinemasApp {

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please define movie title and seating map in [Title] [Row] [SeatsPerRow] format:");
		System.out.print("> ");
		String line = br.readLine();
		if (line == null || line.isBlank())
			return;

		ParsedInit init = parseInit(line.trim());
		if (init == null) {
			System.out.println("Invalid input. Example: Inception 8 10");
			return;
		}
		if (init.rows < 1 || init.rows > 26 || init.cols < 1 || init.cols > 50) {
			System.out.println("Rows must be 1..26 and SeatsPerRow must be 1..50.");
			return;
		}

		CinemaService cinema = new CinemaServiceImpl(init.title, init.rows, init.cols);
		runMenu(br, cinema);
	}

	private static void runMenu(BufferedReader br, CinemaService cinema) throws Exception {
		while (true) {
			System.out.println();
			System.out.println("Welcome to GIC Cinemas");
			System.out.printf("[1] Book tickets for %s (%d seats available)%n", cinema.getTitle(),
					cinema.getAvailableSeatCount());
			System.out.println("[2] Check bookings");
			System.out.println("[3] Exit");
			System.out.print("Please enter your selection:\n> ");
			String sel = br.readLine();
			if (sel == null)
				return;
			sel = sel.trim();
			if (sel.equals("1")) {
				handleBooking(br, cinema);
			}
			else if (sel.equals("2")) {
				handleCheck(br, cinema);
			}
			else if (sel.equals("3")) {
				System.out.println();
				System.out.println("Thank you for using GIC Cinemas system. Bye!");
				return;
			}
			else {
				// ignore invalid, loop again
			}
		}
	}

	private static void handleBooking(BufferedReader br, CinemaService cinema) throws Exception {
		while (true) {
			System.out.print("\nEnter number of tickets to book, or enter blank to go back to main menu:\n> ");
			String s = br.readLine();
			if (s == null)
				return;
			s = s.trim();
			if (s.isEmpty())
				return;
			int n;
			try {
				n = Integer.parseInt(s);
			}
			catch (NumberFormatException ex) {
				continue;
			}
			if (n <= 0)
				continue;
			if (n > cinema.getAvailableSeatCount()) {
				System.out.printf("%nSorry, there are only %d seats available.%n", cinema.getAvailableSeatCount());
				continue;
			}

			List<Seat> seats = cinema.proposeDefaultSeats(n);
			String bookingId = cinema.previewBooking(seats); // temporary hold to show map
			System.out.printf("%nSuccessfully reserved %d %s tickets.%n", n, cinema.getTitle());
			System.out.println("Booking id: " + bookingId);
			System.out.println("Selected seats:");
			System.out.println(SeatMapPrinter.render(cinema.getRows(), cinema.getCols(),
					cinema.getAllBookedSeatsExcept(bookingId), seats));

			while (true) {
				System.out.print("\nEnter blank to accept seat selection, or enter new seating position:\n> ");
				String pos = br.readLine();
				if (pos == null)
					return;
				pos = pos.trim();
				if (pos.isEmpty()) {
					cinema.confirmBooking(bookingId);
					System.out.println("\nBooking id: " + bookingId + " confirmed.");
					return;
				}
				else {
					List<Seat> custom = cinema.proposeFromPosition(bookingId, pos, n);
					if (custom == null) {
						System.out.println("Invalid position. Example: B03");
						continue;
					}
					System.out.println("\nBooking id: " + bookingId);
					System.out.println("Selected seats:");
					System.out.println(SeatMapPrinter.render(cinema.getRows(), cinema.getCols(),
							cinema.getAllBookedSeatsExcept(bookingId), custom));
				}
			}
		}
	}

	private static void handleCheck(BufferedReader br, CinemaService cinema) throws Exception {
		while (true) {
			System.out.print("\nEnter booking id, or enter blank to go back to main menu:\n> ");
			String id = br.readLine();
			if (id == null)
				return;
			id = id.trim();
			if (id.isEmpty())
				return;
			List<Seat> seats = cinema.getSeatsForBooking(id);
			if (seats == null)
				continue;
			System.out.println("\nBooking id: " + id);
			System.out.println("Selected seats:");
			System.out.println(SeatMapPrinter.render(cinema.getRows(), cinema.getCols(),
					cinema.getAllBookedSeatsExcept(id), seats));
		}
	}

	private record ParsedInit(String title, int rows, int cols) {
	}

	private static ParsedInit parseInit(String input) {
		// Split by spaces; last two tokens numeric -> rows & cols; remainder is title
		String[] parts = input.split("\\s+");
		if (parts.length < 3)
			return null;
		try {
			int cols = Integer.parseInt(parts[parts.length - 1]);
			int rows = Integer.parseInt(parts[parts.length - 2]);
			StringBuilder title = new StringBuilder();
			for (int i = 0; i < parts.length - 2; i++) {
				if (i > 0)
					title.append(" ");
				title.append(parts[i]);
			}
			return new ParsedInit(title.toString(), rows, cols);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

}
