package com.satpall.crochet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf().ignoringAntMatchers("/h2-console/**").and().headers().frameOptions().sameOrigin().and()
				.authorizeRequests()
				.antMatchers("/", "/shop", "/product-details/**", "/cart", "/checkout", "/order-success", "/about",
						"/contact", "/faq", "/privacy-policy", "/terms", "/css/**", "/js/**", "/images/**",
						"/uploads/**", "/webjars/**", "/h2-console/**", "/error", "/403", "/404", "/500")
				.permitAll().antMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated().and().formLogin()
				.loginPage("/admin/login").loginProcessingUrl("/admin/login")
				.defaultSuccessUrl("/admin/dashboard", true).failureUrl("/admin/login?error=true").permitAll().and()
				.logout().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true)
				.deleteCookies("JSESSIONID").permitAll();

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withUsername("admin").password(encoder.encode("admin123")).roles("ADMIN").build());
		return manager;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}