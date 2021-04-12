package com.wander.swplanet.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.wander.swplanet.domain.Planet;
import com.wander.swplanet.repository.PlanetRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlanetService {
	
	private final PlanetRepository planetRepository;
	
	public Flux<Planet> findAll() {
		return planetRepository.findAll();
	}
	
	public Mono<Planet> findById(Integer id) {
		return planetRepository.findById(id)
				.switchIfEmpty(monoResponseStatusNotFoundException())
				.log();
	}
	
	public <T> Mono<T> monoResponseStatusNotFoundException() {
		return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Planet not found"));
	}

	public Mono<Planet> save(Planet planet) {
		return planetRepository.save(planet);
	}
	
	public Mono<Void> update(Planet planet) {
		return findById(planet.getId())
				.flatMap(validPlanet -> planetRepository.save(planet))
				.then();
	}

	public Mono<Void> delete(int id) {
		return findById(id)
				.flatMap(planetRepository::delete);
	}

	@Transactional
	public Flux<Planet> saveAll(@Valid List<Planet> planets) {
		return planetRepository.saveAll(planets);
	}
	
}
