package com.cartsafari.service;

import java.util.List;

import com.cartsafari.model.UserDetails;

public interface UserService {
	
	public UserDetails saveUser(UserDetails user);
	
	public UserDetails getUserByEmail(String email);
	
	public List<UserDetails> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);
	
	public void increaseFailedAttempt(UserDetails userDetails);
	
	public void userAccountLock(UserDetails userDetails);
	
	public boolean unlockAccountTimeExpired(UserDetails userDetails);
	
	public void resetAttempt(int userId);

	public void updateUserResetToken(String email, String resetToken);
	
	public UserDetails getUserByToken(String token);
	
	public UserDetails updateUser(UserDetails user);

}
