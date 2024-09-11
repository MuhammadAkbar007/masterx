package uz.akbar.masterx.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uz.akbar.masterx.entity.Reservation;
import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.Slot;
import uz.akbar.masterx.repository.ReservationRepository;

/**
 * ReservationService
 */
@Service
public class ReservationService {

	@Autowired
	ReservationRepository repository;

	public Set<Reservation> findByDate(LocalDate date) {
		return repository.findByDate(date);
	}

	public Reservation findByClient(User client) {
		Optional<Reservation> optional = repository.findByClient(client);

        return optional.orElse(null);
    }

	public boolean reserve(User client, LocalDate date, Slot chosenSlot) {
		try {
			Reservation reservation = new Reservation();
			reservation.setClient(client);
			reservation.setDate(date);
			reservation.setTime(chosenSlot);

			repository.save(reservation);

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
