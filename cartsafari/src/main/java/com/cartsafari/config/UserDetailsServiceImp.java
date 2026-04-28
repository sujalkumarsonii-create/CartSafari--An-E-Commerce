package com.cartsafari.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cartsafari.repository.UserRepository;

@Service
public class UserDetailsServiceImp implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		com.cartsafari.model.UserDetails user = userRepository.findByEmail(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("User Not Found..");
		}
		
		return new CustomUser(user);
	}

}
