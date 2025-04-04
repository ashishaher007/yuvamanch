package com.ymanch.config;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.ymanch.helper.CommonMessages;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig {
	private static final String BEARER_FORMAT = "JWT";
	private static final String SCHEME = "Bearer";
	private static final String SECURITY_SCHEME_NAME = "Security Scheme";

	@Value("${swagger.title}")
	private String swaggerDesc;
//	@Value("${https.server.url}")
//	private String webSiteUrl;

	@Bean
	public OpenAPI api() {
		return new OpenAPI().schemaRequirement(SECURITY_SCHEME_NAME, getSecurityScheme())
				.security(getSecurityRequirement()).info(info());
		// .servers(List.of(new Server().url("https://api1.pcventure.in"))); // Ensure
		// HTTPS

	}

	private Info info() {
		return new Info().title("YUVA MANCH").description(swaggerDesc).version("1.0.0")
				.contact(new Contact().name("Yuva Manch").email("mailId").url("https://yuvamanch.org"));
	}

	private List<SecurityRequirement> getSecurityRequirement() {
		SecurityRequirement securityRequirement = new SecurityRequirement();
		securityRequirement.addList(SECURITY_SCHEME_NAME);
		return List.of(securityRequirement);
	}

	private SecurityScheme getSecurityScheme() {
		SecurityScheme securityScheme = new SecurityScheme();
		securityScheme.bearerFormat(BEARER_FORMAT);
		securityScheme.type(SecurityScheme.Type.HTTP);
		securityScheme.in(SecurityScheme.In.HEADER);
		securityScheme.scheme(SCHEME);
		return securityScheme;
	}

	@Bean
	public ForwardedHeaderFilter forwardedHeaderFilter() {
		return new ForwardedHeaderFilter();
	}

	@Bean
	public CommonMessages commonMessages() {
		return new CommonMessages();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
