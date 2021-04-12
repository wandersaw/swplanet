package com.wander.swplanet.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.wander.swplanet.domain.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Integer> {
	
	Mono<User> findByUsername(String username);
	
}
