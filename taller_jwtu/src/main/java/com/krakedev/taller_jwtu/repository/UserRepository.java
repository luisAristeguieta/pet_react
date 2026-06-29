package com.krakedev.taller_jwtu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krakedev.taller_jwtu.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	  Optional<User> findByUsername(String username);

	    /**
	     * Verifica si existe un usuario con el username dado
	     * @param username Nombre de usuario a verificar
	     * @return true si existe, false en caso contrario
	     */
	    boolean existsByUsername(String username);
	    
	    
}
