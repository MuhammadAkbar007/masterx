package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import uz.akbar.masterx.entity.Reservation;
import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.Profile;
import uz.akbar.masterx.enums.Slot;

/**
 * MessageService
 */
@Service
public class MessageService {

	@Autowired
	KeyboardService keyboardService;

	@Autowired
	UserService userService;

	@Autowired
	SlotService slotService;

	@Autowired
	ReservationService reservationService;

	LocalDate today = LocalDate.now();
	LocalDate tomorrow = today.plusDays(1);
	LocalDate dayAfterTomorrow = today.plusDays(2);
	String day = "";

	public SendMessage handleTextMessage(String msg, Long chatId, String firstName) {
		SendMessage sendMsg;

		switch (msg) {
			case "/start":
				sendMsg = createStartMessage(chatId, firstName);
				break;
			case "/today":
			case "Navbatlarni ko'rish 🫣":
				sendMsg = getToday(chatId);
				break;
			case "/book":
			case "Navbat olish 🙋":
				sendMsg = bookReservation(chatId);
				break;
			default:
				sendMsg = SendMessage.builder()
						.chatId(chatId)
						.text("Buyruqlardan birini tanlang 👇")
						.replyMarkup(keyboardService.showMainMenu())
						.build();
				break;
		}

		return sendMsg;
	}

	public SendMessage handleCallback(String callbackData, long chatId) {
		if (callbackData.startsWith("book_")) {
			return slotService.bookSlot(chatId, callbackData.substring(5), slotService.determineDate(day));
		}

		switch (callbackData) {
			case "today":
			case "tomorrow":
			case "dayAfterTomorrow":
				day = callbackData;
				return sendSlots(chatId, day);
			case "tomorrowReservations":
			case "dayAfterTomorrowReservations":
				return getTomorrow(chatId, callbackData);
			default:
				return SendMessage.builder()
						.chatId(chatId)
						.text("Bunday buyruq mavjud emas! 🙅")
						.build();
		}

	}

	public SendMessage createStartMessage(Long chatId, String firstName) {
		return SendMessage.builder()
				.chatId(chatId)
				.text("Assalomu alaykum " + firstName + " ! Iltimos, davom etish uchun kontaktingizni kiriting 👇")
				.replyMarkup(keyboardService.shareContactKeyboard())
				.build();
	}

	public SendMessage getToday(long chatId) {
		return SendMessage.builder()
				.chatId(chatId)
				.text(getReservationsForAnyDay("today", chatId))
				.replyMarkup(keyboardService.seeTomorrowReservations())
				.build();
	}

	public SendMessage getTomorrow(long chatId, String callbackData) {
		String day = "";

		if (callbackData.equals("tomorrowReservations")) {
			day = "tomorrow";
		} else if (callbackData.equals("dayAfterTomorrowReservations")) {
			day = "dayAfterTomorrow";
		}

		return SendMessage.builder()
				.chatId(chatId)
				.text(getReservationsForAnyDay(day, chatId))
				.replyMarkup(keyboardService.showMainMenu())
				.build();
	}

	public String getReservationsForAnyDay(String day, long chatId) {
		StringBuilder result = new StringBuilder();
		int counter = 1;
		LocalDate date = LocalDate.now();

		if (day.equals("tomorrow")) {
			date = date.plusDays(1);
		} else if (day.equals("dayAfterTomorrow")) {
			date = date.plusDays(2);
		}

		Set<Reservation> reservations = reservationService.findByDate(date);

		if (reservations.isEmpty()) {
			result.append("Bu kun uchun navbat olinmagan! 🙃");
		} else {
			for (Reservation reservation : reservations) {
				String firstName = reservation.getClient().getFirstName();
				String time = reservation.getTime().getTimeRange();

				result.append(counter).append(" | 💇 ").append(firstName).append(" | ⏰ ").append(time);

				User user = userService.findByChatId(chatId);
				if (user != null && (user.getProfile() == Profile.ADMIN || user.getProfile() == Profile.BARBER)) {
					result.append(" | 📞 ").append(reservation.getClient().getPhoneNumber());
				}

				result.append("\n");
				counter++;
			}
		}

		return result.toString();
	}

	public SendMessage bookReservation(long chatId) {
		User user = userService.findByChatId(chatId);

		if (user == null) {
			return SendMessage.builder()
					.chatId(chatId)
					.text("Navbat olish uchun kontaktingizni qoldiring 🤳")
					.replyMarkup(keyboardService.shareContactKeyboard())
					.build();
		}

		Reservation reservation = reservationService.findByClient(user.getId());
		if (reservation != null) {
			return SendMessage.builder()
					.chatId(chatId)
					.text("Sizda " + reservation.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
							+ " kunda 📆 \n" + reservation.getTime().getTimeRange()
							+ " vaqtga ⏰ \nnavbat olingan!")
					.replyMarkup(keyboardService.showMainMenu())
					.build();
		}

		return SendMessage.builder()
				.chatId(chatId)
				.text("Qaysi kunga navbat olmoqchisiz? 🤔")
				.replyMarkup(keyboardService.selectReservationDate(today, tomorrow, dayAfterTomorrow))
				.build();
	}

	public SendMessage handleContact(Long chatId, String firstName, String lastName, String username,
			String phoneNumber, Long tgId) {

		User saved = userService.registerUser(firstName, lastName, username, phoneNumber, tgId, chatId);

		return SendMessage.builder()
				.chatId(chatId)
				.text(saved != null
						? "Tabriklayman " + firstName + ", siz " + saved.getProfile().name()
								+ " sifatida ro'yxatdan o'tdingiz! 🎉"
						: "Xatolik yuz berdi 😬")
				.build();
	}

	public SendMessage showMainMenu(long chatId) {
		return SendMessage.builder()
				.chatId(chatId)
				.text("Quyidagi buyruqlardan birini tanlang 👇")
				.replyMarkup(keyboardService.showMainMenu())
				.build();
	}

	public SendMessage sendSlots(long chatId, String day) {
		Set<Slot> availableSlots = slotService.getAvailableSlots(day);

		if (availableSlots.isEmpty()) {
			return SendMessage.builder()
					.chatId(chatId)
					.text("Bu kun uchun bo'sh vaqt yo'q ekan 😕")
					.build();
		}

		return SendMessage.builder()
				.chatId(chatId)
				.text("O'zingizga qulay vaqtni tanlang ⌚️")
				.replyMarkup(keyboardService.showAvailableTimes(availableSlots))
				.build();
	}

	public void executeMessage(SendMessage message, TelegramClient client) {
		try {
			client.execute(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
