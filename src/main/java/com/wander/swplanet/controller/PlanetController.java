package com.wander.swplanet.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wander.swplanet.domain.Planet;
import com.wander.swplanet.service.PlanetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("planets")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PlanetController {

	private final PlanetService planetService;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<Planet> listAll() {
		log.info(LocalDateTime.now().toString());
		return planetService.findAll();
	}
	
	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Planet> findById(@PathVariable Integer id) {
		return planetService.findById(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Planet> save(@Valid @RequestBody Planet planet) {
		return planetService.save(planet);
	}

	@PostMapping("/batch")
	@ResponseStatus(HttpStatus.CREATED)
	public Flux<Planet> saveBatch(
			@RequestBody 
			@NotEmpty(message = "Input planet list cannot be empty")
			List<@Valid Planet> planets) {
		return planetService.saveAll(planets);
	}
	
	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> update(@PathVariable int id, @Valid @RequestBody Planet planet) {
		return planetService.update(planet.withId(id));
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable int id) {
		return planetService.delete(id);
	}
	
}
