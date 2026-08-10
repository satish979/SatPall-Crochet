package com.satpall.crochet.config;

import com.satpall.crochet.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;

	public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")))
			.authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/shop")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/cart")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/checkout")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/order-success")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/order-payment")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/order-failure")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/my-orders")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/my-orders/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/invoice/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/about")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/api/cart/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/contact")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/product-details/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/Loomellecrochet/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/uploads/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/api/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/admin/login")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN").anyRequest()
					.authenticated())
				.formLogin(form -> form.loginPage("/admin/login").loginProcessingUrl("/admin/login")
						.usernameParameter("username").passwordParameter("password")
						.defaultSuccessUrl("/admin/dashboard", true).failureUrl("/admin/login?error=true").permitAll())
			.rememberMe(remember -> remember.rememberMeParameter("remember-me")
					.key("loomellecrochet-remember-key")
					.tokenValiditySeconds(1209600)
					.userDetailsService(customUserDetailsService)
					.alwaysRemember(true))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())
				.authenticationProvider(authenticationProvider());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
}