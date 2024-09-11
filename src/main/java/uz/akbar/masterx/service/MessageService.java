package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import uz.akbar.masterx.entity.User;
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

	LocalDate today = LocalDate.now();
	LocalDate tomorrow = today.plusDays(1);
	LocalDate dayAfterTomorrow = today.plusDays(2);

	public SendMessage handleTextMessage(String msg, Long chatId, String firstName) {
		SendMessage sendMsg;

		switch (msg) {
			case "/start":
				sendMsg = createStartMessage(chatId, firstName);
				break;
			case "/today":
			case "Navbatlarni ko'rish 🫣":
				// TODO: get today's reservations
				sendMsg = getToday(chatId);
				break;
			case "/book":
			case "Navbat olish 🙋":
				// TODO: book a reservation
				sendMsg = bookReservation(chatId);
				break;
			default:
				sendMsg = SendMessage.builder()
						.chatId(chatId)
						.text("Buyruqlardan birini tanlang 👇")
						.build();
				break;
		}

		return sendMsg;
	}

	public SendMessage handleCallback(String callbackData, long chatId) {
		// book_SLOT_21
		if (callbackData.startsWith("book_")) {
			return bookSlot(chatId, callbackData.substring(5));
		}

		switch (callbackData) {
			case "today":
			case "tomorrow":
			case "dayAfterTomorrow":
				return sendSlots(chatId, callbackData);
			default:
				return SendMessage.builder()
						.chatId(chatId)
						.text("Bunday buyruq mavjud emas! 🙅")
						.build();
		}

	}

	public SendMessage createStartMessage(Long chatId, String firstName) {
		ReplyKeyboardMarkup shareContactKeyboard = keyboardService.shareContactKeyboard();

		return SendMessage.builder()
				.chatId(chatId)
				.text("Assalomu alaykum " + firstName + " ! Iltimos, davom etish uchun kontaktingizni kiriting 👇")
				.replyMarkup(shareContactKeyboard)
				.build();
	}

	public SendMessage getToday(long chatId) {
		return SendMessage.builder()
				.chatId(chatId)
				.text("Bugungi buyurtmalar")
				.build();
	}

	public SendMessage bookReservation(long chatId) {

		if (!userService.existsByChatId(chatId)) {
			ReplyKeyboardMarkup shareContactKeyboard = keyboardService.shareContactKeyboard();

			return SendMessage.builder()
					.chatId(chatId)
					.text("Navbat olish uchun kontaktingizni qoldiring 🤳")
					.replyMarkup(shareContactKeyboard)
					.build();
		}

		InlineKeyboardMarkup markup = keyboardService.selectReservationDate(today, tomorrow, dayAfterTomorrow);

		return SendMessage.builder()
				.chatId(chatId)
				.text("Qaysi kunga navbat olmoqchisiz? 🤔")
				.replyMarkup(markup)
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

		InlineKeyboardMarkup showAvailableTimes = keyboardService.showAvailableTimes(availableSlots);

		return SendMessage.builder()
				.chatId(chatId)
				.text("O'zingizga qulay vaqtni tanlang ⌚️")
				.replyMarkup(showAvailableTimes)
				.build();
	}

	public void executeMessage(SendMessage message, TelegramClient client) {
		try {
			client.execute(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SendMessage bookSlot(long chatId, String slotName) {
		Slot chosenSlot;

		try {
			chosenSlot = Slot.valueOf(slotName);
		} catch (Exception e) {
			return SendMessage.builder()
					.chatId(chatId)
					.text("Noto'g'ri vaqt tanlandi! 🙅")
					.build();
		}

		// boolean successSave = reservationService.reserve(User client, LocalDate date,
		// Slot time);
		String response = ""; // successSave

		return SendMessage.builder()
				.chatId(chatId)
				.text(response)
				.build();
	}

}
