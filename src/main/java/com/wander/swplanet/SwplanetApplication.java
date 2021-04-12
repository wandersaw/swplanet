package com.wander.swplanet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SwplanetApplication {
	
	static {
		BlockHound.install(builder -> 
			builder
				.allowBlockingCallsInside("java.util.UUID", "randomUUID")
				.allowBlockingCallsInside("javax.net.ssl.SSLContext", "init"));
	}

	public static void main(String[] args) {
		SpringApplication.run(SwplanetApplication.class, args);
	}

}
