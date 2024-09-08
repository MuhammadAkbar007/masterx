package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 * KeyboardService
 */
@Service
public class KeyboardService {

	public ReplyKeyboardMarkup shareContactKeyboard() {

		KeyboardRow row = new KeyboardRow();
		row.add(KeyboardButton.builder()
				.text("Kontaktni kiritish 🤳")
				.requestContact(true)
				.build());

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row);

		return ReplyKeyboardMarkup.builder()
				.keyboard(keyboard)
				.resizeKeyboard(true)
				.oneTimeKeyboard(true)
				.build();
	}

	public ReplyKeyboardMarkup showMainMenu() {
		KeyboardRow row = new KeyboardRow();
		row.add(new KeyboardButton("Navbatlarni ko'rish 🫣"));
		row.add(new KeyboardButton("Navbat olish 🙋"));

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row);

		return ReplyKeyboardMarkup.builder()
				.keyboard(keyboard)
				.resizeKeyboard(true)
				.oneTimeKeyboard(false)
				.build();
	}

	public InlineKeyboardMarkup selectReservationDate(LocalDate today, LocalDate tomorrow, LocalDate dayAfterTomorrow) {
		return InlineKeyboardMarkup.builder()
				.keyboardRow(
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("Bugun 📅 " + today.getDayOfMonth() + "-" + today.getMonthValue() + "-"
										+ today.getYear())
								.callbackData("today")
								.build()))
				.keyboardRow(
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("Ertaga 📅 " + tomorrow.getDayOfMonth() + "-" + tomorrow.getMonthValue() + "-"
										+ tomorrow.getYear())
								.callbackData("tomorrow")
								.build())

				)
				.keyboardRow(
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("Indinga 📅 " + dayAfterTomorrow.getDayOfMonth() + "-"
										+ dayAfterTomorrow.getMonthValue() + "-"
										+ dayAfterTomorrow.getYear())
								.callbackData("dayAfterTomorrow")
								.build()))
				.build();
	}

}
