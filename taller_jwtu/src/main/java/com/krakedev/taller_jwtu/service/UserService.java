package com.krakedev.taller_jwtu.service;

import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.krakedev.taller_jwtu.entity.User;
import com.krakedev.taller_jwtu.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User save(User user) {
		// Verify if the unique account username already exists in persistence
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new RuntimeException("The username '" + user.getUsername() + "' is already registered");
		}

		// Securely hash the plain-text password using BCrypt salt algorithms before
		String encryptedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		user.setPassword(encryptedPassword);

		return userRepository.save(user);
	}

	public User authenticate(String username, String password) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Identity account not found: " + username));

		// Validate plain-text payload against secure database hash streams using BCrypt
		if (!BCrypt.checkpw(password, user.getPassword())) {
			throw new RuntimeException("Invalid matching password credentials for user: " + username);
		}

		return user;
	}

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public boolean existsUsername(String username) {
		return userRepository.existsByUsername(username);
	}
}