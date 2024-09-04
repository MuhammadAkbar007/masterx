package uz.akbar.masterx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import jakarta.annotation.PostConstruct;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MasterXBot
 */
@Component
public class MasterXBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

	private TelegramClient telegramClient;

	@Autowired
	Logger logger;

	@Value("${telegram.bot.token}")
	private String botToken;

	public MasterXBot() {
		telegramClient = null;
	}

	@PostConstruct
	private void init() {
		telegramClient = new OkHttpTelegramClient(getBotToken());
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

	@Override
	public LongPollingUpdateConsumer getUpdatesConsumer() {
		return this;
	}

	@Override
	public void consume(Update update) {
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

			logger.log(userFirstName, userUsername, Long.toString(userId), messageText, message.getText());

			try {
				telegramClient.execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}

		}
	}

}
