package com.krakedev.taller_jwtu.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.krakedev.taller_jwtu.entity.User;
import com.krakedev.taller_jwtu.service.TokenBlackListService;
import com.krakedev.taller_jwtu.service.UserService;
import com.krakedev.taller_jwtu.util.Util;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserService userService;
	private final TokenBlackListService blacklistService;

	public AuthController(UserService userService, TokenBlackListService blacklistService) {
		this.userService = userService;
		this.blacklistService = blacklistService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		try {
			User savedUser = userService.save(user);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("User successfully registered: " + savedUser.getUsername());
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getLocalizedMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Internal error triggered during user registration pipeline");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
		String username = credentials.get("username");
		String password = credentials.get("password");

		try {
			User authenticatedUser = userService.authenticate(username, password);
			String token = Util.generarToken(authenticatedUser.getUsername(), authenticatedUser.getRol());

			return ResponseEntity.ok(Map.of("token", token, "username", authenticatedUser.getUsername(), "rol",
					authenticatedUser.getRol()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Authentication rejected: " + e.getLocalizedMessage());
		}
	}

	@GetMapping("/perfil")
	public ResponseEntity<?> viewProfile() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		String securityAuthority = auth.getAuthorities().iterator().next().getAuthority();
		String cleanRole = securityAuthority.replace("ROLE_", "");

		return ResponseEntity.ok(Map.of("message", "Welcome to the secure boundary authenticated session profile",
				"username", username, "detected_role", cleanRole, "status", "Successfully Authorized"));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			blacklistService.invalidateToken(token);

			return ResponseEntity
					.ok(Map.of("message", "Session closed successfully. Token cryptographic block revoked"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Malformed routing metadata request: Missing Authorization Bearer header");
		}
	}

	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> adminDashboard() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		return ResponseEntity.ok(Map.of("message", "Welcome to the administrator premium restriction workspace layout",
				"admin_username", username));
	}
}