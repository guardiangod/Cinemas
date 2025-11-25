package app.service;

import app.model.Booking;
import app.model.Seat;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CinemaServiceImpl implements CinemaService {

	private final String title;

	private final int rows;

	private final int cols;

	private final boolean[][] taken; // confirmed seats

	private final Map<String, Booking> bookings = new LinkedHashMap<>();

	private int sequence = 1;

	private final Map<String, List<Seat>> previews = new HashMap<>(); // bookingId ->
																		// proposed seats
																		// (not yet
																		// confirmed)

	public CinemaServiceImpl(String title, int rows, int cols) {
		this.title = title;
		this.rows = rows;
		this.cols = cols;
		this.taken = new boolean[rows][cols];
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getCols() {
		return cols;
	}

	@Override
	public int getAvailableSeatCount() {
		int total = rows * cols;
		int used = 0;
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				if (taken[r][c])
					used++;
		return total - used;
	}

	@Override
	public List<Seat> proposeDefaultSeats(int count) {
		List<Seat> result = new ArrayList<>();
		int startRow = 0; // furthest from screen: 'A' == 0
		for (int r = startRow; r < rows && result.size() < count; r++) {
			List<Integer> colsOrder = defaultColumnOrder();
			for (int c : colsOrder) {
				if (!taken[r][c]) {
					result.add(new Seat(r, c));
					if (result.size() == count)
						break;
				}
			}
		}
		return result;
	}

	private List<Integer> defaultColumnOrder() {
		// middle-most, then strictly to the right; finally wrap left (if any)
		List<Integer> order = new ArrayList<>(cols);
		int midLeft = (cols - 1) / 2; // for even, left-middle
		for (int c = midLeft; c < cols; c++)
			order.add(c);
		for (int c = 0; c < midLeft; c++)
			order.add(c);
		return order;
	}

	@Override
	public String previewBooking(List<Seat> seats) {
		String id = String.format("GIC%04d", sequence++);
		previews.put(id, seats);
		return id;
	}

	@Override
	public void confirmBooking(String bookingId) {
		List<Seat> seats = previews.remove(bookingId);
		if (seats == null)
			return;
		Booking b = new Booking(bookingId);
		for (Seat s : seats) {
			if (!taken[s.row][s.col]) {
				taken[s.row][s.col] = true;
				b.seats.add(s);
			}
		}
		bookings.put(bookingId, b);
	}

	@Override
	public List<Seat> proposeFromPosition(String bookingId, String position, int count) {
		Seat start = parsePosition(position);
		if (start == null)
			return null;
		if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols)
			return null;

		List<Seat> selection = new ArrayList<>();
		// fill to the right on the same row
		for (int c = start.col; c < cols && selection.size() < count; c++) {
			if (!taken[start.row][c])
				selection.add(new Seat(start.row, c));
		}
		// overflow using default from next row
		for (int r = start.row + 1; r < rows && selection.size() < count; r++) {
			List<Integer> order = defaultColumnOrder();
			for (int c : order) {
				if (!taken[r][c])
					selection.add(new Seat(r, c));
				if (selection.size() == count)
					break;
			}
		}
		previews.put(bookingId, selection);
		return selection;
	}

	private static final Pattern POS = Pattern.compile("^([A-Z])(\\d{1,2})$");

	private Seat parsePosition(String pos) {
		Matcher m = POS.matcher(pos.toUpperCase(Locale.ROOT));
		if (!m.matches())
			return null;
		char rowChar = m.group(1).charAt(0);
		int row = rowChar - 'A';
		int col = Integer.parseInt(m.group(2)) - 1;
		return new Seat(row, col);
	}

	@Override
	public List<Seat> getSeatsForBooking(String bookingId) {
		Booking b = bookings.get(bookingId);
		if (b == null)
			return null;
		return new ArrayList<>(b.seats);
	}

	@Override
	public Set<Seat> getAllBookedSeatsExcept(String bookingId) {
		Set<Seat> set = new HashSet<>();
		for (Booking b : bookings.values()) {
			if (!b.id.equals(bookingId))
				set.addAll(b.seats);
		}
		return set;
	}

}
