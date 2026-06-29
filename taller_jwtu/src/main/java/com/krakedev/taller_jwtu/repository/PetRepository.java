package com.krakedev.taller_jwtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krakedev.taller_jwtu.entity.Pet;

public interface PetRepository extends JpaRepository <Pet, Integer>{

}
