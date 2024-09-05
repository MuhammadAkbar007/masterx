package uz.akbar.masterx;

import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * BotController
 */
@Controller
public class BotController {

	@Autowired
	Logger logger;

	@Autowired
	BotService service;

	public void handleUpdate(Update update, TelegramClient client) {

		if (update.hasMessage()) {

			if (update.getMessage().hasText()) {

				String messageText = update.getMessage().getText();
				long chatId = update.getMessage().getChatId();
				String userFirstName = update.getMessage().getChat().getFirstName();
				String userUsername = update.getMessage().getChat().getUserName();
				long userId = update.getMessage().getChat().getId();

				SendMessage message = SendMessage
						.builder()
						.chatId(chatId)
						.text(messageText)
						.build();

				switch (messageText) {
					case "/start":
						message = service.handleStart(chatId, userFirstName);
						break;
				}

				logger.log(userFirstName, userUsername, Long.toString(userId), messageText, message.getText());

				try {
					client.execute(message);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			if (update.getMessage().hasContact()) {

				Contact contact = update.getMessage().getContact();
				Long chatId = update.getMessage().getChatId();

				String firstName = contact.getFirstName(); // required
				String lastName = contact.getLastName(); // optional
				String username = update.getMessage().getChat().getUserName(); // Akbar_Ahmad
				String phoneNumber = contact.getPhoneNumber(); // +998945060749
				Long tgId = contact.getUserId() != null ? contact.getUserId() : chatId;

				// todo: register user & respond his role

				String res = "Rahmat " + phoneNumber;

				logger.log(firstName, username, tgId.toString(), "Kontakt: " + phoneNumber, res);

				SendMessage msg = SendMessage.builder()
						.chatId(chatId)
						.text(res)
						.build();

				try {
					client.execute(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}
}
