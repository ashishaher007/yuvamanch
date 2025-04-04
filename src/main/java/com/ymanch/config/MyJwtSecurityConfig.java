package com.ymanch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ymanch.service.CustomJwtUserDetailServcie;

@Configuration
@EnableWebSecurity
public class MyJwtSecurityConfig {
	@Autowired
	private CustomJwtUserDetailServcie customUserDetailsService;

	@Autowired
	private JwtAuthenticationFilter jwtFilter;

	@Autowired
	private AuthenticationEntryPoint entryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

//	@Bean
//	public AuthenticationManager authenticationManager() throws Exception {
//		return super.authenticationManager();
//	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.customUserDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource))
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/health", "/actuator/info")
						.permitAll()
						.requestMatchers("/v3/api-docs/**", "/stomp/**", "/wss/**", "/configuration/ui",
								"/swagger-resources/**", "/configuration/security", "/swagger-ui/**", "/webjars/**",
								"/ymanch/users/user/userLogin", "/ymanch/users/user/userRegister",
								"/ymanch/admin/adminRegister", "/ymanch/supadmin/eventcatg/add",
								"/ymanch/users/chatmessage/history/{senderId}/{receiverId}",

								"/ymanch/users/chatmessage/**", "/ymanch/users/district/getAllDistricts",
								"/ymanch/users/user/forgetPassword/{emailId}", "/ymanch/users/user/validatePassword",
								"/ymanch/admin/adminLogin", "/ymanch/supadmin/eventcatg/getDisputeTitles",
								"/ymanch/supadmin/eventcatg/updateUserUUID",
								"/ymanch/users/chatmessage/history/group/{groupId}",
								"/uploads/**")

						.permitAll().requestMatchers("/ws/**", "/stomp/**").permitAll() // Allow WebSocket connections
						.requestMatchers("/ymanch/users/**").hasRole("USER").requestMatchers("/ymanch/users/**")
						.hasRole("USER").requestMatchers("/ymanch/admin/**").hasAnyRole("ADMIN", "SUPER-ADMIN").anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint));

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
