package com.wander.swplanet.integration;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.wander.swplanet.domain.Planet;
import com.wander.swplanet.repository.PlanetRepository;
import com.wander.swplanet.util.PlanetCreator;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class PlanetControllerIT {
	
	private static final String ADMIN = "vader";
	private static final String USER = "skywalker";
	
	@MockBean
	private PlanetRepository planetRepositoryMock;
	
	@Autowired
	private WebTestClient webTestClient;
	
	private final Planet planetValid = PlanetCreator.createValidPlanet();
	
	@BeforeAll
	public static void blockHoundSetup() {
		BlockHound.install(builder -> 
		builder
			.allowBlockingCallsInside("javax.net.ssl.SSLContext", "init"));
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
		
		BDDMockito.when(planetRepositoryMock.delete(ArgumentMatchers.any(Planet.class)))
			.thenReturn(Mono.empty());
	
		BDDMockito.when(planetRepositoryMock.save(PlanetCreator.createValidUpdatedPlanet()))
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
	@DisplayName("listAll returns a flux of planet - logged user: admin")
	@WithUserDetails(ADMIN)
	public void listAll_ReturnFluxOfPlanet_WhenSuccesful_List_Admin() {
		webTestClient
			.get()
			.uri("/planets")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Planet.class)
			.hasSize(1)
			.contains(planetValid);
	}
	
	@Test
	@DisplayName("listAll returns a flux of planet - logged user: user")
	@WithUserDetails(USER)
	public void listAll_ReturnFluxOfPlanet_WhenSuccesful_List_User() {
		webTestClient
			.get()
			.uri("/planets")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Planet.class)
			.hasSize(1)
			.contains(planetValid);
	}
	
	@Test
	@DisplayName("listAll unauthorized when invalid user")
	public void listAll_Unauthorized_InvalidUser() {
		webTestClient
			.get()
			.uri("/planets")
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}
	
	@Test
	@DisplayName("findById returns mono with planet when exists - logged user: admin")
	@WithUserDetails(ADMIN)
	public void findById_ReturnMonoPlanet_WhenSuccessful_Admin() {
		webTestClient
			.get()
			.uri("/planets/{id}", 1)
			.exchange()
			.expectStatus()
			.isOk() 
			.expectBody(Planet.class)
			.isEqualTo(planetValid);
	}
	
	@Test
	@DisplayName("findById returns mono with planet when exists - logged user: user")
	@WithUserDetails(USER)
	public void findById_ReturnMonoPlanet_WhenSuccessful_User() {
		webTestClient
			.get()
			.uri("/planets/{id}", 1)
			.exchange()
			.expectStatus()
			.isOk() 
			.expectBody(Planet.class)
			.isEqualTo(planetValid);
	}
	
	@Test
	@DisplayName("findById unauthorized when invalid user")
	public void findById_Unauthorized_InvalidUser() {
		webTestClient
			.get()
			.uri("/planets/{id}", 1)
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}
	
	@Test
	@DisplayName("findById returns mono error when planet does not exist - any user authenticated")
	@WithUserDetails(ADMIN)
	public void findById_ReturnMonoError_WhenEmptyMonoIsReturned_AnyUser() {
		BDDMockito.when(planetRepositoryMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.empty());
		
		webTestClient
			.get()
			.uri("/planets/{id}", 2)
			.exchange()
			.expectStatus()
			.isNotFound() 
			.expectBody()
			.jsonPath("$.status").isEqualTo("404");
	}
	
	@Test
	@DisplayName("save creates a planet when successful - logged user: admin")
	@WithUserDetails(ADMIN)
	public void save_CreatesPlanet_WhenSuccessful_Admin() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		webTestClient
			.post()
			.uri("/planets")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(planetToSave))
			.exchange()
			.expectStatus()
			.isCreated()
			.expectBody(Planet.class)
			.isEqualTo(planetValid);
	}
	
	@Test
	@DisplayName("save return forbidden when not admin user")
	@WithUserDetails(USER)
	public void save_Forbidden_User() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		webTestClient
			.post()
			.uri("/planets")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(planetToSave))
			.exchange()
			.expectStatus()
			.isForbidden();
	}
	
	@Test
	@DisplayName("save unauthorized when invalid user")
	public void save_Unauthorized_InvalidUser() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		webTestClient
			.post()
			.uri("/planets")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(planetToSave))
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}
	
	@Test
	@DisplayName("saveBatch creates a list of planet when successful - logged user: admin")
	@WithUserDetails(ADMIN)
	public void saveBatch_CreatesListPlanet_WhenSuccessful_Admin() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		webTestClient
			.post()
			.uri("/planets/batch")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(List.of(planetToSave, planetToSave)))
			.exchange()
			.expectStatus()
			.isCreated()
			.expectBodyList(Planet.class)
			.hasSize(2)
			.contains(planetValid);
	}
	
	@Test
	@DisplayName("saveBatch return forbidden when not admin user")
	@WithUserDetails(USER)
	public void saveBatch_Forbidden_User() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		webTestClient
			.post()
			.uri("/planets/batch")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(List.of(planetToSave, planetToSave)))
			.exchange()
			.expectStatus()
			.isForbidden();
	}
	
	@Test
	@DisplayName("saveBatch return unauthorized when invalid user")
	public void saveBatch_Forbidden_InvalidUser() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		webTestClient
			.post()
			.uri("/planets/batch")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(List.of(planetToSave, planetToSave)))
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}
	
	@Test
	@DisplayName("saveBatch returns Mono error when one of planets contains null or empty name - user logged: admin")
	@WithUserDetails(ADMIN)
	public void saveBatch_ReturnsMonoError_ContainsNullOrEmptyName() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved();
		
		BDDMockito.when(planetRepositoryMock
				.saveAll(ArgumentMatchers.anyIterable()))
				.thenReturn(Flux.just(planetValid, planetValid.withName("")));
		
		webTestClient
			.post()
			.uri("/planets/batch")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(List.of(planetToSave, planetToSave.withName(""))))
			.exchange()
			.expectStatus()
			.is5xxServerError()
			.expectBody()
			.jsonPath("$.status").isEqualTo("500");
	}
	
	@Test
	@DisplayName("save returns error when name is empty - logged user: admin")
	@WithUserDetails(ADMIN)
	public void save_ReturnsError_WhenNameIsEmpy() {
		Planet planetToSave = PlanetCreator.createPlanetToBeSaved().withName("");
		
		webTestClient
			.post()
			.uri("/planets")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(planetToSave))
			.exchange()
			.expectStatus()
			.isBadRequest()
			.expectBody()
			.jsonPath("$.status").isEqualTo("400");
	}
	
	@Test
	@DisplayName("delete removes the planet when successful - logged user: admin")
	@WithUserDetails(ADMIN)
	public void delete_RemovesPlanet_WhenSuccessful_Admin() {
		webTestClient
			.delete()
			.uri("/planets/{id}", 2)
			.exchange()
			.expectStatus()
			.isNoContent();
	}
	
	@Test
	@DisplayName("delete return forbidden when not admin user")
	@WithUserDetails(USER)
	public void delete_Forbidden_User() {
		webTestClient
			.delete()
			.uri("/planets/{id}", 2)
			.exchange()
			.expectStatus()
			.isForbidden();
	}
	
	@Test
	@DisplayName("delete return unauthorized when invalid user")
	public void delete_Unauthorized_InvalidUser() {
		webTestClient
			.delete()
			.uri("/planets/{id}", 2)
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}

	@Test
	@DisplayName("delete returns mono error when planet does not exist - logged user: admin")
	@WithUserDetails(ADMIN)
	public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(planetRepositoryMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.empty());
		
		webTestClient
			.delete()
			.uri("/planets/{id}", 2)
			.exchange()
			.expectStatus()
			.isNotFound() 
			.expectBody()
			.jsonPath("$.status").isEqualTo("404");
	}
	
	@Test
	@DisplayName("update save updated planet and return mono when successful - logged user: admin")
	@WithUserDetails(ADMIN)
	public void update_SaveUpdatedPlanet_WhenSuccessful_Admin() {
		webTestClient
			.put()
			.uri("/planets/{id}", 1)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(PlanetCreator.createValidUpdatedPlanet()))
			.exchange()
			.expectStatus()
			.isNoContent();
	}
	
	@Test
	@DisplayName("update return forbidden when not admin user")
	@WithUserDetails(USER)
	public void update_Forbidden_User() {
		webTestClient
			.put()
			.uri("/planets/{id}", 1)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(PlanetCreator.createValidUpdatedPlanet()))
			.exchange()
			.expectStatus()
			.isForbidden();
	}

	@Test
	@DisplayName("update return unauthorized when invalid user")
	public void update_Unauthorized_InvalidUser() {
		webTestClient
			.put()
			.uri("/planets/{id}", 1)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(PlanetCreator.createValidUpdatedPlanet()))
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}
	
	@Test
	@DisplayName("update returns mono error when planet does not exist - logged user: admin")
	@WithUserDetails(ADMIN)
	public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(planetRepositoryMock.findById(ArgumentMatchers.anyInt()))
			.thenReturn(Mono.empty());
	
		webTestClient
			.put()
			.uri("/planets/{id}", 2)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(planetValid))
			.exchange()
			.expectStatus()
			.isNotFound() 
			.expectBody()
			.jsonPath("$.status").isEqualTo("404");
	}
	
}
