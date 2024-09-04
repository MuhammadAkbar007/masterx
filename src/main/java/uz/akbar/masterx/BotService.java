package uz.akbar.masterx;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * BotService
 */
@Service
public class BotService {

	public SendMessage handleStart(SendMessage msg, String firstName) {

		msg.setText("Assalomu alaykum " + firstName + "! Iltimos, davom etish uchun kontaktingizni kiriting 👇");
		// msg.setReplyMarkup(); // send keyboard for user to share contact
		return msg;
	}

}
