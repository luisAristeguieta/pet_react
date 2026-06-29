package com.krakedev.taller_jwtu.controller;

import com.krakedev.taller_jwtu.entity.Pet;
import com.krakedev.taller_jwtu.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/pets")
public class PetController {

	private final PetRepository petRepository;

	public PetController(PetRepository petRepository) {
		this.petRepository = petRepository;
	}

	@GetMapping
	public ResponseEntity<List<Pet>> getAllPets() {
		return ResponseEntity.ok(petRepository.findAll());
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerPet(@RequestParam("name") String name, @RequestParam("breed") String breed,
			@RequestParam("file") MultipartFile file) {
		try {
			if (file.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Creation rejected: A valid image upload stream is mandatory");
			}

			Pet pet = new Pet(name, breed, file.getContentType(), file.getBytes());
			Pet savedPet = petRepository.save(pet);

			return ResponseEntity.status(HttpStatus.CREATED).body(savedPet);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Execution crash intercepted during pet registration pipeline: " + e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePet(@PathVariable("id") Integer id) {
		try {
			if (!petRepository.existsById(id)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Deletion failure: Target identity resource not found for ID: " + id);
			}
			petRepository.deleteById(id);
			return ResponseEntity.ok("Resource context successfully dropped from server registers");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Execution error processing resource drop context: " + e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updatePet(@PathVariable("id") Integer id, @RequestParam("name") String name,
			@RequestParam("breed") String breed, @RequestParam(value = "file", required = false) MultipartFile file) {
		try {
			Optional<Pet> optionalPet = petRepository.findById(id);
			if (optionalPet.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Mutation target missing: Pet record not found for ID: " + id);
			}

			Pet existingPet = optionalPet.get();
			existingPet.setName(name);
			existingPet.setBreed(breed);

			if (file != null && !file.isEmpty()) {
				existingPet.setMimeType(file.getContentType());
				existingPet.setPhoto(file.getBytes());
			}

			Pet updatedPet = petRepository.save(existingPet);
			return ResponseEntity.ok(updatedPet);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Mutation process crashed within persistence layer pipelines: " + e.getMessage());
		}
	}

	@GetMapping("/{id}/photo")
	public ResponseEntity<byte[]> getPetPhoto(@PathVariable("id") Integer id) {
		return petRepository.findById(id)
				.map(pet -> ResponseEntity.ok().header("Content-Type", pet.getMimeType()).body(pet.getPhoto()))
				.orElse(ResponseEntity.notFound().build());
	}

}