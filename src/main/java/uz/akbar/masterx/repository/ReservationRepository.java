package uz.akbar.masterx.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.akbar.masterx.entity.Reservation;
import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.ReservationStatus;

/**
 * ReservationRepository
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

	List<Reservation> findByDateAndStatus(LocalDate date, ReservationStatus status);

	Optional<Reservation> findByClientIdAndStatus(UUID clientId, ReservationStatus status);

	List<Reservation> findAllByDateBeforeAndStatus(LocalDate date, ReservationStatus status);

	List<Reservation> findByStatus(ReservationStatus active);

	List<Reservation> findByClient(User user);

	List<Reservation> findByDate(LocalDate date);
}
