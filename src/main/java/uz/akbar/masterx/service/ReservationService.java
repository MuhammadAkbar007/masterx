package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import uz.akbar.masterx.util.Logger;
import uz.akbar.masterx.util.Notification;
import uz.akbar.masterx.entity.Reservation;
import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.ReservationStatus;
import uz.akbar.masterx.enums.Slot;
import uz.akbar.masterx.repository.ReservationRepository;

/**
 * ReservationService
 */
@Service
public class ReservationService {

	@Autowired
	ReservationRepository repository;

	@Autowired
	Logger logger;

	@Autowired
	Notification notification;

	public List<Reservation> findAllActives() {
		return repository.findByStatus(ReservationStatus.ACTIVE);
	}

	public List<Reservation> findByDate(LocalDate date) {
		return repository.findByDateAndStatus(date, ReservationStatus.ACTIVE);
	}

	public Reservation findByClient(UUID userId) {
		Optional<Reservation> optional = repository.findByClientIdAndStatus(userId, ReservationStatus.ACTIVE);

		return optional.orElse(null);
	}

	public boolean reserve(User client, LocalDate date, Slot chosenSlot) {
		try {
			Reservation reservation = new Reservation();
			reservation.setClient(client);
			reservation.setDate(date);
			reservation.setTime(chosenSlot);
			reservation.setStatus(ReservationStatus.ACTIVE);

			repository.save(reservation);

			notification.notifyBarber(client.getFirstName() + " (" + client.getPhoneNumber() + ") 📞\n"
					+ reservation.getDate() + " kuni " + reservation.getTime().getTimeRange() + " vaqtga ⏰\n"
					+ "navbat oldi ✅");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Run expiration on startup
	@EventListener(ContextRefreshedEvent.class)
	public void onStartUpEvent() {
		checkAndExpireOldReservations();
	}

	// Expire reservations every hour
	@Scheduled(cron = "0 0 0 * * *")
	public void expireOldReservations() {
		checkAndExpireOldReservations();
	}

	public void checkAndExpireOldReservations() {
		LocalDate today = LocalDate.now();

		// expire Before today
		List<Reservation> expiredReservations = repository.findAllByDateBeforeAndStatus(today,
				ReservationStatus.ACTIVE);

		for (Reservation reservation : expiredReservations) {
			reservation.setStatus(ReservationStatus.EXPIRED);
			repository.save(reservation);
			logger.log(reservation.getId().toString(), "from ACTIVE", "to EXPIRED");
		}

		// expire Today before Time
		List<Reservation> todayReservations = repository.findByDateAndStatus(today, ReservationStatus.ACTIVE);

		for (Reservation reservation : todayReservations) {
			if (reservation.getTime().getEndTime().isBefore(LocalTime.now())) {
				reservation.setStatus(ReservationStatus.EXPIRED);
				repository.save(reservation);
				logger.log(reservation.getId().toString(), "from ACTIVE", "to EXPIRED");
			}
		}
	}

	public String deleteReservation(String id) {
		Optional<Reservation> optional = repository.findById(UUID.fromString(id));

		if (optional.isEmpty())
			return "Bunday buyurtma mavjud emas! 🙅";

		try {
			Reservation reservation = optional.get();
			reservation.setStatus(ReservationStatus.CANCELLED);
			repository.save(reservation);

			User client = reservation.getClient();
			notification.notifyBarber(
					client.getFirstName() + " (" + client.getPhoneNumber() + ") 📞\n" + reservation.getDate() + " kuni "
							+ reservation.getTime().getTimeRange() + " vaqtdagi ⏰\n" + "navbati bekor qilindi ☑️");

			return client.getFirstName() + "ning navbati muvaffaqqiyatli o'chirildi! ✅";
		} catch (Exception e) {
			e.printStackTrace();
			return "Xatolik yuz berdi! 🤷";
		}
	}

	public List<Reservation> getReports(User user) {
		return repository.findByClient(user);
	}

	public List<Reservation> getAllByDate(LocalDate date) {
		return repository.findByDate(date);
	}
}
