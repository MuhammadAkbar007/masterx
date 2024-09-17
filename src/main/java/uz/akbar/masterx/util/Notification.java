package uz.akbar.masterx.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import uz.akbar.masterx.service.UserService;
import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.Profile;

/**
 * Notification
 */
@Service
public class Notification {

	@Autowired
	UserService userService;

	@Value("${telegram.bot.token}")
	private String botToken;

	private final RestTemplate restTemplate = new RestTemplate();

	public void notifyBarber(String message) {
		User barber = userService.findBarber(Profile.ADMIN);
		// User barber = userService.findBarber(Profile.BARBER);

		if (barber != null) {
			String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);

			String requestBody = String.format("{\"chat_id\":\"%s\",\"text\":\"%s\"}", barber.getChatId(), message);

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");

			HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			System.out.println("Response: " + response.getBody());
		}
	}

}
