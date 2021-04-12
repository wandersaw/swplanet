package com.wander.swplanet.controller;

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

import com.wander.swplanet.domain.Planet;
import com.wander.swplanet.service.PlanetService;
import com.wander.swplanet.util.PlanetCreator;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class PlanetControllerTest {

	@InjectMocks
	private PlanetController planetController;
	
	@Mock
	private PlanetService planetServiceMock;
	
	private final Planet planetValid = PlanetCreator.createValidPlanet();
	
	@BeforeAll
	public static void blockHoundSetup() {
		BlockHound.install();
	}
	
	@BeforeEach
	public void setUp() {
		BDDMockito.when(planetServiceMock.findAll())
			.thenReturn(Flux.just(planetValid));
		
		BDDMockito.when(planetServiceMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(planetValid));
		
		BDDMockito.when(planetServiceMock.save(PlanetCreator.createPlanetToBeSaved()))
			.thenReturn(Mono.just(planetValid));
		
		BDDMockito.when(planetServiceMock
				.saveAll(List.of(PlanetCreator.createPlanetToBeSaved(), PlanetCreator.createPlanetToBeSaved())))
				.thenReturn(Flux.just(planetValid, planetValid));
		
		BDDMockito.when(planetServiceMock.delete(ArgumentMatchers.anyInt())).
			thenReturn(Mono.empty());
		
		BDDMockito.when(planetServiceMock.update(PlanetCreator.createValidUpdatedPlanet()))
			.thenReturn(Mono.empty());
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
	
	@Test
	@DisplayName("listAll returns a flux of planet")
	public void listAll_ReturnFluxOfPlanet_WhenSuccesful() {
		StepVerifier.create(planetController.listAll())
			.expectSubscription()
			.expectNext(planetValid)
			.verifyComplete();
	}
	
	@Test
	@DisplayName("findById returns mono with planet when exists")
	public void findById_ReturnMonoPlanet_WhenSuccessful() {
		StepVerifier.create(planetController.findById(1))
			.expectSubscription()
			.expectNext(planetValid)
			.verifyComplete();
	}
	
	@Test
	@DisplayName("save creates a planet when successful")
	public void save_CreatesPlanet_WhenSuccessful() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		StepVerifier.create(planetController.save(planetToSave))
			.expectSubscription()
			.expectNext(planetValid)
			.verifyComplete();
	}
	
	@Test
	@DisplayName("saveBatch creates a list of planet when successful")
	public void saveBatch_CreatesListPlanet_WhenSuccessful() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		StepVerifier.create(planetController.saveBatch(List.of(planetToSave, planetToSave)))
			.expectSubscription()
			.expectNext(planetValid, planetValid)
			.verifyComplete();
	}
	
	@Test
	@DisplayName("delete removes the planet when successful")
	public void delete_RemovesPlanet_WhenSuccessful() {
		StepVerifier.create(planetController.delete(1))
			.expectSubscription()
			.verifyComplete();
	}
	
	@Test
	@DisplayName("update save updated planet and return mono when successful")
	public void update_SaveUpdatedPlanet_WhenSuccessful() {
		StepVerifier.create(planetController.update(1, PlanetCreator.createValidUpdatedPlanet()))
			.expectSubscription()
			.verifyComplete();
	}
	
}
