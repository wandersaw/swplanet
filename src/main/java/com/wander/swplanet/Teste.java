package com.wander.swplanet;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

public class Teste {
	
	public static void main(String[] args) {
		System.out.println("Rebel: " + PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("rebel"));
		System.out.println("Empire: " + PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("empire"));
	}

}
