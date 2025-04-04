package com.ymanch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${https.server.url}")
	private String httpsMainUrl;
	@Value("${http.server.url}")
	private String httpMainUrl;
	@Value("${https.backend.server.url}")
	private String httpsBackendMainUrl;
	@Value("${http.backend.server.url}")
	private String httpBackendMainUrl;
	@Value("${https.server.admin.url}")
	private String httpsAdminMainUrl;
	@Value("${http.server.admin.url}")
	private String httpAdminMainUrl;

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(50); // Minimum number of threads
		executor.setMaxPoolSize(200); // Maximum number of threads
		executor.setQueueCapacity(1000); // Queue size for holding tasks
		executor.setThreadNamePrefix("websocket-"); // Thread name prefix
		executor.initialize();
		return executor;
	}

	// old config
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOriginPatterns("http://15.207.221.78:8080", "http://192.168.0.105:8081", "http://localhost",
						"http://192.168.0.120:3000", "http://localhost:5500", "http://192.168.0.117:3000",
						"192.168.12.210:3000", "http://localhost:8081", "http://localhost:8084", httpsMainUrl,
						httpMainUrl, httpsBackendMainUrl, httpBackendMainUrl, httpsAdminMainUrl, httpAdminMainUrl,
						"http://192.168.1.8:3000", "http://localhost:3000", "http://localhost:5173","https://superadmin.strishakti.org")
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/user", "/topic", "/queue"); // Message broker for queues
		registry.setApplicationDestinationPrefixes("/app"); // Prefix for client messages
	}

}
