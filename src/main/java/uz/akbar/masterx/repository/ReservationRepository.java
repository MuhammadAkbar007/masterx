package uz.akbar.masterx.repository;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.akbar.masterx.entity.Reservation;

/**
 * ReservationRepository
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Set<Reservation> findByDate(LocalDate date);
}
