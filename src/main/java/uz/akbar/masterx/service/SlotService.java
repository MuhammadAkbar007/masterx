package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	public List<Slot> getAvailableSlots(String day) {
		LocalDate date = determineDate(day);
		LocalTime currentTime = LocalTime.now();

		List<Reservation> reservations = reservationService.findByDate(date);

		List<Slot> reservedTimes = reservations.stream()
				.map(Reservation::getTime)
				.collect(Collectors.toList());

		return Arrays.stream(Slot.values())
				.filter(slot -> {
					LocalTime slotStartTime = parseSlotStartTime(slot);
					return (date.isAfter(LocalDate.now()) || slotStartTime.isAfter(currentTime))
							&& !reservedTimes.contains(slot);
				})
				.sorted(Comparator.comparing(slot -> parseSlotStartTime(slot)))
				.collect(Collectors.toList());

	}

	private LocalTime parseSlotStartTime(Slot slot) {
		String startTime = slot.getTimeRange().split(" - ")[0];
		return LocalTime.parse(startTime);
	}

	public SendMessage bookSlot(long chatId, String slotName, LocalDate date) {
		Slot chosenSlot;

		try {
			chosenSlot = Slot.valueOf(slotName);
		} catch (Exception e) {
			e.printStackTrace();
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
				? "Sizni " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " kuni 📆 \n"
						+ chosenSlot.getTimeRange()
						+ " vaqtda ⏰ \nro'yxatga yozib qo'ydim 🙌"
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
