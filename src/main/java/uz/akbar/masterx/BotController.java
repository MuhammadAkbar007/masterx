package uz.akbar.masterx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.Profile;
import uz.akbar.masterx.repository.UserRepository;

/**
 * BotController
 */
@Controller
public class BotController {

	@Autowired
	Logger logger;

	@Autowired
	BotService service;

	@Autowired
	UserRepository userRepository;

	public void handleUpdate(Update update, TelegramClient client) {

		String firstName = update.getMessage().getChat().getFirstName();
		String lastName = update.getMessage().getChat().getLastName();
		String username = update.getMessage().getChat().getUserName();
		Long tgId = update.getMessage().getChat().getId();
		Long chatId = update.getMessage().getChatId();

		if (update.hasMessage()) {

			// For text messages
			if (update.getMessage().hasText()) {

				String messageText = update.getMessage().getText();

				SendMessage message = SendMessage
						.builder()
						.chatId(chatId)
						.text(messageText)
						.build();

				switch (messageText) {
					case "/start":
						message = service.handleStart(chatId, firstName);
						break;
				}

				logger.log(firstName, username, Long.toString(tgId), messageText, message.getText());

				try {
					client.execute(message);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			// For sharing Contact
			if (update.getMessage().hasContact()) {

				String phoneNumber = update.getMessage().getContact().getPhoneNumber();

				logger.log(firstName, username, Long.toString(tgId), "Contact has been shared",
						"Kontakt = " + phoneNumber);

				Profile profile;
				String role;
				if (phoneNumber.equals("+998945060749")) {
					profile = Profile.ADMIN;
					role = "Admin";
				} else if (phoneNumber.equals("+998993912111")) {
					profile = Profile.BARBER;
					role = "Sartarosh";
				} else {
					profile = Profile.CLIENT;
					role = "Mijoz";
				}

				User user = new User();
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setUsername(username);
				user.setPhoneNumber(phoneNumber);
				user.setTgId(Long.toString(tgId));
				user.setProfile(profile);

				boolean success = false;

				try {
					userRepository.save(user);
					success = true;
				} catch (Exception e) {
					e.printStackTrace();
				}

				String response = success
						? "Tabriklayman " + firstName + ", siz " + role + " sifatida ro'yxatdan o'tdingiz! 🎉"
						: "Xatolik yuz berdi 😬";

				SendMessage message = SendMessage.builder()
						.chatId(chatId)
						.text(response)
						.build();

				logger.log(firstName, username, Long.toString(tgId), "user saving", response);

				try {
					client.execute(message);
				} catch (Exception e) {
					e.printStackTrace();
				}

				message.setText("Quyidagilardan birini tanlang 👇");
				message.setReplyMarkup(service.showMainMenu());

				try {
					client.execute(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}
}
