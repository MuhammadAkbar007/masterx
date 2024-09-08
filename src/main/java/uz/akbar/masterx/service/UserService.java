package uz.akbar.masterx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uz.akbar.masterx.entity.User;
import uz.akbar.masterx.enums.Profile;
import uz.akbar.masterx.repository.UserRepository;

/**
 * UserService
 */
@Service
public class UserService {

	@Autowired
	UserRepository repository;

	public User registerUser(String firstName, String lastName, String username, String phoneNumber, Long tgId) {
		if (repository.existsByPhoneNumber(phoneNumber)) {
			return null;
		}

		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setPhoneNumber(phoneNumber);
		user.setTgId(Long.toString(tgId));
		user.setProfile(determineProfile(phoneNumber));

		try {
			return repository.save(user);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Profile determineProfile(String phoneNumber) {
		if (phoneNumber.equals("+998945060749")) {
			return Profile.ADMIN;
		} else if (phoneNumber.equals("+998993912111")) {
			return Profile.BARBER;
		} else {
			return Profile.CLIENT;
		}
	}

}
