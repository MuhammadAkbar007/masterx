package uz.akbar.masterx;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 * BotService
 */
@Service
public class BotService {

	public SendMessage handleStart(long chatId, String firstName) {
		KeyboardRow row = new KeyboardRow();
		row.add(KeyboardButton.builder()
				.text("Kontaktni kiritish 🤳")
				.requestContact(true)
				.build());

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row);

		ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
				.keyboard(keyboard)
				.resizeKeyboard(true)
				.oneTimeKeyboard(true)
				.build();

		return SendMessage.builder()
				.chatId(chatId)
				.text("Assalomu alaykum " + firstName + "! Iltimos, davom etish uchun kontaktingizni kiriting 👇")
				.replyMarkup(replyKeyboardMarkup)
				.build();
	}

	public ReplyKeyboardMarkup showMainMenu() {
		KeyboardRow row = new KeyboardRow();
		row.add(new KeyboardButton("/navbatlarni ko'rish"));
		row.add(new KeyboardButton("/navbat olish"));

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row);

		return ReplyKeyboardMarkup.builder()
				.keyboard(keyboard)
				.resizeKeyboard(true)
				.oneTimeKeyboard(false)
				.build();
	}
}
