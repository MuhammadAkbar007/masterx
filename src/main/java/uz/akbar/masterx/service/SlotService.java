package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uz.akbar.masterx.entity.Reservation;
import uz.akbar.masterx.enums.Slot;
import uz.akbar.masterx.repository.ReservationRepository;

/**
 * SlotService
 */
@Service
public class SlotService {

	@Autowired
	ReservationRepository reservationRepository;

	public Set<Slot> getAvailableSlots(String day) {
		LocalDate date = LocalDate.now();

		if (day.equals("tomorrow")) {
			date = date.plusDays(1);
		} else if (day.equals("dayAfterTomorrow")) {
			date = date.plusDays(2);
		}

		Set<Slot> allSlots = EnumSet.allOf(Slot.class);

		Set<Reservation> reservations = reservationRepository.findByDate(date);

		Set<Slot> bookedSlots = new HashSet<>();
		for (Reservation reservation : reservations) {
			bookedSlots.add(reservation.getTime());
		}

		Set<Slot> availableSlots = new HashSet<>();
		for (Slot slot : allSlots) {
			if (!bookedSlots.contains(slot)) {
				availableSlots.add(slot);
			}
		}

		return availableSlots;
	}

}
