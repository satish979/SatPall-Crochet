package com.satpall.crochet.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if ("admin".equals(username)) {

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			//String password = "admin123";

			return new User("admin", "$2a$10$QVAVz11IUh16wjMlgdFjcOTwxmyunpVx/NV4Lfcyq8Nsy0gFHu6DK",
					Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
		}

		throw new UsernameNotFoundException("Invalid username");
	}

}
