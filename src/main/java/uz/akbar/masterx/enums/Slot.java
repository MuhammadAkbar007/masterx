package uz.akbar.masterx.enums;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Slot
 */
public enum Slot {

	SLOT_9("09:00 - 10:00"),
	SLOT_10("10:00 - 11:00"),
	SLOT_11("11:00 - 12:00"),
	SLOT_13("13:00 - 14:00"),
	SLOT_14("14:00 - 15:00"),
	SLOT_15("15:00 - 16:00"),
	SLOT_16("16:00 - 17:00"),
	SLOT_17("17:00 - 18:00"),
	SLOT_18("18:00 - 19:00"),
	SLOT_19("19:00 - 20:00"),
	SLOT_20("20:00 - 21:00"),
	SLOT_21("21:00 - 22:00"),
	SLOT_22("22:00 - 23:00");

	private final String timeRange;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

	Slot(String timeRange) {
		this.timeRange = timeRange;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public LocalTime getStartTime() {
		return LocalTime.parse(timeRange.split(" - ")[0], formatter);
	}

	public LocalTime getEndTime() {
		return LocalTime.parse(timeRange.split(" - ")[1], formatter);
	}
}
