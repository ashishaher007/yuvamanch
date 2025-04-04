package com.ymanch.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Value("${https.server.url}")
	private String httpsMainUrl;
	@Value("${http.server.url}")
	private String httpMainUrl;
	@Value("${https.server.admin.url}")
	private String httpsAdminMainUrl;
	@Value("${http.server.admin.url}")
	private String httpAdminMainUrl;
	@Value("${https.backend.server.url}")
	private String httpsBackendMainUrl;
	@Value("${http.backend.server.url}")
	private String httpBackendMainUrl;
	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Autowired
	private RequestLoggingInterceptor requestLoggingInterceptor;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://localhost:5173",
				"http://192.168.0.105:8084", "http://192.168.0.117:3000", "http://192.168.0.120:3000",
				"http://localhost", "http://localhost:5500", httpMainUrl, httpsMainUrl, httpsBackendMainUrl,
				httpBackendMainUrl, httpsAdminMainUrl, httpAdminMainUrl, "https://localhost:8084",
				"http://localhost:8081", "http://192.168.1.8:3000", "192.168.12.210:3000", "http://127.0.0.1:5500",
				"https://superadmin.strishakti.org"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestLoggingInterceptor);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if ("local".equals(activeProfile)) {
			registry.addResourceHandler("/uploads/**").addResourceLocations("file:/C:/var/www/uploads/")
					.setCachePeriod(0);
		} else if ("dev".equals(activeProfile)) {
			registry.addResourceHandler("/uploads/**").addResourceLocations("file:/var/www/uploads/").setCachePeriod(0);
		}
	}
}
