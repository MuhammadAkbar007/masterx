package uz.akbar.masterx.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.Profile;

/**
 * UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByChatId(String chatId);

	Optional<User> findByChatId(String chatId);

	Optional<User> findByProfile(Profile profile);
}
