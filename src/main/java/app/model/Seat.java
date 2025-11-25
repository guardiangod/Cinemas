package app.model;

import java.util.Objects;

public class Seat {

	public final int row; // 0-based, 0 = 'A' (furthest from screen)

	public final int col; // 0-based

	public Seat(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public char rowLabel() {
		return (char) ('A' + row);
	}

	public int colNumber() {
		return col + 1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Seat seat = (Seat) o;
		return row == seat.row && col == seat.col;
	}

	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}

	@Override
	public String toString() {
		return "" + rowLabel() + String.format("%02d", colNumber());
	}

}
