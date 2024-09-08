package uz.akbar.masterx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Logger
 */
@Component
public class Logger {

	public void log(String name, String txt, String botAnswer) {

		System.out.println("\n--------------------");
		System.out.println("| *** Log Info *** |");
		System.out.println("--------------------");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));

		System.out.println("Message from: " + name + "\n Text: " + txt);
		System.out.println("Bot answer: \n Text: " + botAnswer);
	}

}
