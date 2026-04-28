package com.cartsafari.service.imp;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cartsafari.model.UserDetails;
import com.cartsafari.repository.UserRepository;
import com.cartsafari.service.UserService;
import com.cartsafari.util.AppConstant;

@Service
public class UserServiceImp implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails saveUser(UserDetails user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDetails saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public UserDetails getUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserDetails> getUsers(String role) {
		
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<UserDetails> byId = userRepository.findById(id);
		if(byId.isPresent()) {
			UserDetails userDetails = byId.get();
			userDetails.setIsEnable(status);
			userRepository.save(userDetails);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDetails userDetails) {
		int attempt = userDetails.getFailedAttempt() + 1;
		userDetails.setFailedAttempt(attempt);
		userRepository.save(userDetails);
	}

	@Override
	public void userAccountLock(UserDetails userDetails) {
		userDetails.setAccountNonLocked(false);
		userDetails.setLockTime(new Date());
		userRepository.save(userDetails);
		
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDetails userDetails) {
		long lockTime = userDetails.getLockTime().getTime();
		long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		
		long timeMillis = System.currentTimeMillis();
		if(unlockTime < timeMillis) {
			userDetails.setAccountNonLocked(true);
			userDetails.setFailedAttempt(0);
			userDetails.setLockTime(null);
			userRepository.save(userDetails);
			return true;
		}
		
		return false;
	}

	@Override
	public void resetAttempt(int userId) {
		
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDetails findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
		
	}

	@Override
	public UserDetails getUserByToken(String token) {
		
		return userRepository.findByResetToken(token);
	}

	@Override
	public UserDetails updateUser(UserDetails user) {
		return userRepository.save(user);
		
	}

}
