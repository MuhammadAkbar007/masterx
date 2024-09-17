package uz.akbar.masterx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import uz.akbar.masterx.util.Logger;
import uz.akbar.masterx.service.MessageService;
import uz.akbar.masterx.service.UserService;

/**
 * BotController
 */
@Controller
public class BotController {

	@Autowired
	MessageService messageService;

	@Autowired
	UserService userService;

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
				if (!userService.existsByChatId(chatId)) {
					String phoneNumber = message.getContact().getPhoneNumber();
					logger.log(firstName, "Contact shared!", "Contact = " + phoneNumber);

					SendMessage sendMsg = messageService.handleContact(chatId, firstName, lastName, username,
							phoneNumber,
							tgId);
					messageService.executeMessage(sendMsg, client);
					logger.log(firstName, "Register", sendMsg.getText());

					SendMessage mainMenuMsg = messageService.showMainMenu(chatId);
					messageService.executeMessage(mainMenuMsg, client);
					logger.log(firstName, "After registration", "Showing main menu");
				} else {
					String response = "Siz allaqachon ro'yxatdan o'tgansiz! ☑️";

					SendMessage sendMsg = SendMessage.builder()
							.chatId(chatId)
							.text(response)
							.build();

					messageService.executeMessage(sendMsg, client);
					logger.log(firstName, "Contact shared!", response);

					SendMessage showMainMenu = messageService.showMainMenu(chatId);
					messageService.executeMessage(showMainMenu, client);
					logger.log(firstName, "Contact shared - continuation", showMainMenu.getText());
				}
			}

		}

		if (update.hasCallbackQuery()) {
			String callbackData = update.getCallbackQuery().getData();
			Long chatId = update.getCallbackQuery().getMessage().getChatId();
			String firstName = update.getCallbackQuery().getFrom().getFirstName();

			SendMessage sendMsg = messageService.handleCallback(callbackData, chatId);
			messageService.executeMessage(sendMsg, client);
			logger.log(firstName, callbackData, sendMsg.getText());
		}

	}

}
