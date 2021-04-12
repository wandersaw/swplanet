package com.wander.swplanet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.wander.swplanet.service.UserService;

@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf().disable()
				.authorizeExchange()
					.pathMatchers(HttpMethod.POST, "/planets/**").hasRole("ADMIN")
					.pathMatchers(HttpMethod.GET, "/planets/**").hasRole("USER")
					.anyExchange().hasRole("ADMIN")
				.and()
					.formLogin()
				.and()
					.httpBasic()
				.and()
					.build();
	}
	
	@Bean
	public ReactiveAuthenticationManager authenticationManager(UserService userService) {
		return new UserDetailsRepositoryReactiveAuthenticationManager(userService);
	}
	
//	@Bean
//	public MapReactiveUserDetailsService userDetailsService() {
//		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//		
//		UserDetails user = User.withUsername("skywalker")
//			.password(passwordEncoder.encode("rebel"))
//			.roles("USER")
//			.build();
//
//		UserDetails admin = User.withUsername("vader")
//			.password(passwordEncoder.encode("empire"))
//			.roles("ADMIN", "USER")
//			.build();
//		
//		return new MapReactiveUserDetailsService(user, admin);
//	}
	
}
