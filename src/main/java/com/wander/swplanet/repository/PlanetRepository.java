package com.wander.swplanet.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.wander.swplanet.domain.Planet;

import reactor.core.publisher.Mono;

public interface PlanetRepository extends ReactiveCrudRepository<Planet, Integer> {
	
	public Mono<Planet> findById(Integer id);

}
