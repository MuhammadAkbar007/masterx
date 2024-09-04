package uz.akbar.masterx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import jakarta.annotation.PostConstruct;

/**
 * MasterXBot
 */
@Component
public class MasterXBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

	private TelegramClient telegramClient;

	@Autowired
	BotController controller;

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
		controller.handleUpdate(update, telegramClient);
	}

}
