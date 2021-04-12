package com.wander.swplanet.util;

import com.wander.swplanet.domain.Planet;

public class PlanetCreator {
	
	public static Planet createPlanetToBeSaved() {
		return Planet.builder()
				.name("Earth")
				.climate("temperate")
				.terrain("sea, earth, mountain, arid, florest")
				.build();
	}
	
	public static Planet createValidPlanet() {
		return Planet.builder()
				.id(1)
				.name("Earth")
				.climate("temperate")
				.terrain("sea, mountain, arid, forest")
				.build();
	}
	
	public static Planet createValidUpdatedPlanet() {
		return Planet.builder()
				.id(1)
				.name("Earth")
				.climate("temperate, hot, artic")
				.terrain("sea, grass, urban, mountain, desert, forest")
				.build();
	}

}
