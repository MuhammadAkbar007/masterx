package uz.akbar.masterx.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.masterx.entity.User;

/**
 * UserRepository
 */
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByPhoneNumber(String phoneNumber);
}
