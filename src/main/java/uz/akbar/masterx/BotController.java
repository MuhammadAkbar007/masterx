package uz.akbar.masterx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
		if (update.hasMessage() && update.getMessage().hasText()) {

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
					message = service.handleStart(message, userFirstName);
					break;
			}

			logger.log(userFirstName, userUsername, Long.toString(userId), messageText, message.getText());

			try {
				client.execute(message);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
