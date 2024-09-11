package uz.akbar.masterx.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.akbar.masterx.entity.Reservation;
import uz.akbar.masterx.entity.User;

/**
 * ReservationRepository
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

	Set<Reservation> findByDate(LocalDate date);

	Optional<Reservation> findByClient(User client);

	boolean existsByClient(User client);
}
