package com.wander.swplanet.service;

import java.util.List;
import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import com.wander.swplanet.domain.Planet;
import com.wander.swplanet.repository.PlanetRepository;
import com.wander.swplanet.util.PlanetCreator;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class PlanetServiceTest {
	
	@InjectMocks
	private PlanetService planetService;
	
	@Mock
	private PlanetRepository planetRepositoryMock;
	
	private final Planet planetValid = PlanetCreator.createValidPlanet();
	
	@BeforeAll
	public static void blockHoundSetup() {
		BlockHound.install();
	}
	
	@Test
	public void blockHoundWorks() {
		try {
			FutureTask<?> task = new FutureTask<>(() -> {
				Thread.sleep(8);
				return "";
			});
			Schedulers.parallel().schedule(task);
		} catch (Exception e) {
			Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
		}
	}
	
	@BeforeEach
	public void setUp() {
		BDDMockito.when(planetRepositoryMock.findAll())
			.thenReturn(Flux.just(planetValid));
		
		BDDMockito.when(planetRepositoryMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(planetValid));
		
		BDDMockito.when(planetRepositoryMock.save(PlanetCreator.createPlanetToBeSaved()))
			.thenReturn(Mono.just(planetValid));
		
		BDDMockito.when(planetRepositoryMock
				.saveAll(List.of(PlanetCreator.createPlanetToBeSaved(), PlanetCreator.createPlanetToBeSaved())))
				.thenReturn(Flux.just(planetValid, planetValid));
		
		BDDMockito.when(planetRepositoryMock.delete(ArgumentMatchers.any(Planet.class))).
			thenReturn(Mono.empty());
		
		BDDMockito.when(planetRepositoryMock.save(PlanetCreator.createValidUpdatedPlanet()))
			.thenReturn(Mono.empty());
	}
	
	@Test
	@DisplayName("findAll returns a flux of planet")
	public void findAll_ReturnFluxOfPlanet_WhenSuccesful() {
		StepVerifier.create(planetService.findAll())
			.expectSubscription()
			.expectNext(planetValid)
			.verifyComplete();
	}
	
	@Test
	@DisplayName("findById returns mono with planet when exists")
	public void findById_ReturnMonoPlanet_WhenSuccessful() {
		StepVerifier.create(planetService.findById(1))
			.expectSubscription()
			.expectNext(planetValid)
			.verifyComplete();
	}
	
	@Test
	@DisplayName("findById returns mono error when planet does not exist")
	public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(planetRepositoryMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.empty());
		
		StepVerifier.create(planetService.findById(1))
			.expectSubscription()
			.expectError(ResponseStatusException.class)
			.verify();
	}
	
	@Test
	@DisplayName("save creates a planet when successful")
	public void save_CreatesPlanet_WhenSuccessful() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		StepVerifier.create(planetService.save(planetToSave))
			.expectSubscription()
			.expectNext(planetValid)
			.verifyComplete();
	}

	@Test
	@DisplayName("saveAll creates a list of planet when successful")
	public void saveAll_CreatesListPlanet_WhenSuccessful() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		StepVerifier.create(planetService.saveAll(List.of(planetToSave, planetToSave)))
			.expectSubscription()
			.expectNext(planetValid, planetValid)
			.verifyComplete();
	}
	
	@Test
	@DisplayName("delete removes the planet when successful")
	public void delete_RemovesPlanet_WhenSuccessful() {
		StepVerifier.create(planetService.delete(1))
			.expectSubscription()
			.verifyComplete();
	}

	@Test
	@DisplayName("delete returns mono error when planet does not exist")
	public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(planetRepositoryMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.empty());
		
		StepVerifier.create(planetService.delete(1))
			.expectSubscription()
			.expectError(ResponseStatusException.class)
			.verify();
	}
	
	@Test
	@DisplayName("update save updated planet and return mono when successful")
	public void update_SaveUpdatedPlanet_WhenSuccessful() {
		StepVerifier.create(planetService.update(PlanetCreator.createValidUpdatedPlanet()))
			.expectSubscription()
			.verifyComplete();
	}
	
	@Test
	@DisplayName("update returns mono error when planet does not exist")
	public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(planetRepositoryMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.empty());
	
		StepVerifier.create(planetService.update(PlanetCreator.createValidUpdatedPlanet()))
			.expectSubscription()
			.expectError(ResponseStatusException.class)
			.verify();
	}
	
}
