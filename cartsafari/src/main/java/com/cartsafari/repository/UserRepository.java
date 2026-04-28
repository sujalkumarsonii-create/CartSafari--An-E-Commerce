package com.cartsafari.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartsafari.model.UserDetails;

public interface UserRepository extends JpaRepository<UserDetails, Integer>{

	public UserDetails findByEmail(String email);
	
	public List<UserDetails> findByRole(String role);
	
	public UserDetails findByResetToken(String token);
}
