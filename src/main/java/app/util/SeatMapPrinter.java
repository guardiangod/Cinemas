package app.util;

import app.model.Seat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatMapPrinter {

	public static String render(int rows, int cols, Set<Seat> confirmedOtherBookings, List<Seat> currentSelection) {
		Set<Seat> cur = new HashSet<>(currentSelection);
		StringBuilder sb = new StringBuilder();
		sb.append("\n  S C R E E N\n");
		sb.append("  ").append("-".repeat(Math.max(0, cols * 2 + 1))).append("\n");
		for (int r = rows - 1; r >= 0; r--) { // top visual row is closest to screen
			char rowLabel = (char) ('A' + r);
			sb.append(rowLabel).append(" ");
			for (int c = 0; c < cols; c++) {
				Seat s = new Seat(r, c);
				char ch = '.';
				if (confirmedOtherBookings.contains(s))
					ch = '#';
				else if (cur.contains(s))
					ch = 'o';
				sb.append(ch).append(' ');
			}
			sb.append("\n");
		}
		sb.append("  ");
		for (int c = 1; c <= cols; c++)
			sb.append(c).append(' ');
		sb.append("\n");
		return sb.toString();
	}

}
