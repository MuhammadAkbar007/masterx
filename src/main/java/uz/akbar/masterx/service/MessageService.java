package uz.akbar.masterx.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import uz.akbar.masterx.entity.User;

/**
 * MessageService
 */
@Service
public class MessageService {

	@Autowired
	KeyboardService keyboardService;

	@Autowired
	UserService userService;

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
			case "book":
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
		String response = "";

		switch (callbackData) {
			case "today":
				response = "Sizni bugungi ro'yxatga yozib qo'ydim! 📝";
				break;
			case "tomorrow":
				response = "Sizni ertagalik ro'yxatga yozib qo'ydim! 📝";
				break;
			case "dayAfterTomorrow":
				response = "Sizni indingilik ro'yxatga yozib qo'ydim! 📝";
				break;
			default:
				response = "Bunday buyruq mavjud emas! 🙅";
				break;
		}

		return SendMessage.builder()
				.chatId(chatId)
				.text(response)
				.build();
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

		InlineKeyboardMarkup markup = keyboardService.selectReservationDate(today, tomorrow, dayAfterTomorrow);

		return SendMessage.builder()
				.chatId(chatId)
				.text("Qaysi kunga navbat olmoqchisiz? 🤔")
				.replyMarkup(markup)
				.build();
	}

	public SendMessage handleContact(Long chatId, String firstName, String lastName, String username,
			String phoneNumber, Long tgId) {
		User saved = userService.registerUser(firstName, lastName, username, phoneNumber, tgId);

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

	public void executeMessage(SendMessage message, TelegramClient client) {
		try {
			client.execute(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
