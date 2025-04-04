package com.team8.project2.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomAuthenticationFilter customAuthenticationFilter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// ✅ CORS 설정 적용
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			// ✅ 요청별 인증/인가 설정
			.authorizeHttpRequests(authorize -> authorize

				// 🔹 Swagger UI 접근 허용
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

				// 🔹 특정 API 엔드포인트에 대한 인증 예외
				.requestMatchers(HttpMethod.GET, "/api/v1/playlists", "/api/v1/playlists/{id}").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/members/**", "/api/v1/members/{id}").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/members/members").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/members/**", "/api/v1/members/{id}").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/curation/**").permitAll()
				.requestMatchers(HttpMethod.PUT, "/api/v1/curation/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/curation/**").permitAll()
				.requestMatchers(HttpMethod.DELETE, "/api/v1/curation/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/curations/**").permitAll()
				.requestMatchers(HttpMethod.PUT, "/api/v1/curations/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/curations/**").permitAll()
				.requestMatchers(HttpMethod.DELETE, "/api/v1/curations/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/images/upload").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/link/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/link/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/admin/**").permitAll()
				.requestMatchers(HttpMethod.DELETE, "api/v1/admin/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/h2-console/**").permitAll()


				// 권한 설정u
				.requestMatchers("/api/v1/posts/statistics").hasRole("ADMIN")
				// 🔹 h2-console 접근 허용
				.requestMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/h2-console/**").permitAll()


				// 🔹 그 외 모든 요청 인증 필요
				.anyRequest().authenticated()
			)
			/*.formLogin(login -> login
					.loginProcessingUrl("/api/v1/members/login")
					.permitAll()// 로그인 요청 URL 지정
				)*/
			.headers((headers) -> headers
				.addHeaderWriter(new XFrameOptionsHeaderWriter(
					XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
			// ✅ CSRF 비활성화 (API 사용을 위해 필수)
			.csrf(csrf -> csrf.disable())
			.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


		return http.build();
	}

	// ✅ CORS 설정
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOriginPatterns(List.of("*"));// 허용할 프론트엔드 주소
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Forwarded-For", "X-Real-IP"));
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
