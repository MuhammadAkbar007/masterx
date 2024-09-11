package uz.akbar.masterx.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.akbar.masterx.entity.User;

/**
 * UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByChatId(String chatId);
}
