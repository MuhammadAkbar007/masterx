package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import uz.akbar.masterx.entity.Reservation;
import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.Slot;

/**
 * SlotService
 */
@Service
public class SlotService {

	@Autowired
	ReservationService reservationService;

	@Autowired
	UserService userService;

	@Autowired
	KeyboardService keyboardService;

	public Set<Slot> getAvailableSlots(String day) {
		LocalDate date = determineDate(day);

		Set<Slot> allSlots = EnumSet.allOf(Slot.class);

		Set<Reservation> reservations = reservationService.findByDate(date);

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

	public SendMessage bookSlot(long chatId, String slotName, LocalDate date) {
		Slot chosenSlot;

		try {
			chosenSlot = Slot.valueOf(slotName);
		} catch (Exception e) {
			return SendMessage.builder()
					.chatId(chatId)
					.text("Noto'g'ri vaqt tanlandi! 🙅")
					.build();
		}

		Optional<User> optionalUser = userService.repository.findByChatId(Long.toString(chatId));
		if (optionalUser.isEmpty()) {
			return SendMessage.builder()
					.chatId(chatId)
					.text("Avval ro'yxatdan o'tishingiz kerak! 🙆")
					.replyMarkup(keyboardService.shareContactKeyboard())
					.build();
		}

		User client = optionalUser.get();

		boolean successSaved = reservationService.reserve(client, date, chosenSlot);
		String response = successSaved
				? "Sizni " + date + " kuni 📆 \n" + chosenSlot.getTimeRange()
						+ " vaqtda ⏰ \n ro'yxatga yozib qo'ydim 🙌"
				: "Xatolik yuz berdi! 🤷";

		return SendMessage.builder()
				.chatId(chatId)
				.text(response)
				.build();
	}

	public LocalDate determineDate(String day) {
		LocalDate date = LocalDate.now();

		if (day.equals("tomorrow")) {
			date = date.plusDays(1);
		} else if (day.equals("dayAfterTomorrow")) {
			date = date.plusDays(2);
		}

		return date;
	}
}
