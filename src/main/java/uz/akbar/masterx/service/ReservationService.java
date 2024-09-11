package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import uz.akbar.masterx.Logger;
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

	public Set<Reservation> findByDate(LocalDate date) {
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

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void expireOldReservations() {
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
		Set<Reservation> todayReservations = repository.findByDateAndStatus(today, ReservationStatus.ACTIVE);

		for (Reservation reservation : todayReservations) {
			if (reservation.getTime().getEndTime().isBefore(LocalTime.now())) {
				reservation.setStatus(ReservationStatus.EXPIRED);
				repository.save(reservation);
				logger.log(reservation.getId().toString(), "from ACTIVE", "to EXPIRED");
			}
		}
	}
}
