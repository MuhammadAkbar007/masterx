package uz.akbar.masterx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import uz.akbar.masterx.service.MessageService;

/**
 * BotController
 */
@Controller
public class BotController {

	@Autowired
	MessageService messageService;

	@Autowired
	Logger logger;

	public void handleUpdate(Update update, TelegramClient client) {

		if (update.hasMessage()) {
			Message message = update.getMessage();
			String text = message.getText();
			String firstName = message.getChat().getFirstName();
			String lastName = message.getChat().getLastName();
			String username = message.getChat().getUserName();
			Long tgId = message.getChat().getId();
			Long chatId = message.getChatId();

			if (message.hasText()) {
				SendMessage sendMsg = messageService.handleTextMessage(text, chatId, firstName);
				messageService.executeMessage(sendMsg, client);
				logger.log(firstName, text, sendMsg.getText());
			}

			if (message.hasContact()) {
				String phoneNumber = message.getContact().getPhoneNumber();
				logger.log(firstName, "Contact shared!", "Contact = " + phoneNumber);

				SendMessage sendMsg = messageService.handleContact(chatId, firstName, lastName, username, phoneNumber,
						tgId);
				messageService.executeMessage(sendMsg, client);
				logger.log(firstName, "Register", sendMsg.getText());

				SendMessage mainMenuMsg = messageService.showMainMenu(chatId);
				messageService.executeMessage(mainMenuMsg, client);
				logger.log(firstName, "After registration", "Showing main menu");
			}

		}

		if (update.hasCallbackQuery()) {
			String callbackData = update.getCallbackQuery().getData();
			Long chatId = update.getCallbackQuery().getMessage().getChatId();

			SendMessage sendMsg = messageService.handleCallback(callbackData, chatId);
			messageService.executeMessage(sendMsg, client);
			// TODO:Logger
			// logger.log(name, txt, botAnswer);
		}

	}

}
