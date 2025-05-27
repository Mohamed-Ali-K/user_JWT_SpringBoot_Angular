package com.kenis.usermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static com.kenis.usermanager.constant.FileConstant.USER_FOLDER;

/**
 * Main Spring Boot application class for the User Manager application.
 * This class is responsible for bootstrapping the application and defining
 * application-level configurations such as CORS settings and the password encoder bean.
 */
@SpringBootApplication
public class UserManagerApplication {

	/**
	 * Standard main method. Initializes and runs the Spring Boot application.
	 * Creates the user folder if it doesn't exist.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserManagerApplication.class, args);
		new File(USER_FOLDER).mkdirs();
	}


	/**
	 * Configures and provides a CorsFilter bean for handling Cross-Origin Resource Sharing (CORS).
	 * Sets allowed origins, headers, methods, and credentials.
	 * @return A configured {@code CorsFilter} instance.
	 */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration(
				"/**", corsConfiguration
		);
		return new CorsFilter(
				urlBasedCorsConfigurationSource
		);
	}

	/**
	 * Provides a BCryptPasswordEncoder bean for encoding passwords.
	 * @return A {@code BCryptPasswordEncoder} instance.
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder (){
		return new BCryptPasswordEncoder();
	}

}
