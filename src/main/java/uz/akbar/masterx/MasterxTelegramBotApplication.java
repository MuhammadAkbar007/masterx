package uz.akbar.masterx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MasterxTelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(MasterxTelegramBotApplication.class, args);
	}

}
