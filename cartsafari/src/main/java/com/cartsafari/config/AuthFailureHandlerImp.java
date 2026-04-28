package com.cartsafari.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.cartsafari.model.UserDetails;
import com.cartsafari.repository.UserRepository;
import com.cartsafari.service.UserService;
import com.cartsafari.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImp extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
	        HttpServletResponse response,
	        AuthenticationException exception)
	        throws IOException, ServletException {

	    String email = request.getParameter("username");

	    UserDetails userDetails = userRepository.findByEmail(email);

	    // ✅ FIX: handle null user
	    if (userDetails == null) {
	        setDefaultFailureUrl("/signin?error");
	        super.onAuthenticationFailure(request, response, exception);
	        return;
	    }

	    if (userDetails.getIsEnable()) {

	        if (userDetails.getAccountNonLocked()) {

	            if (userDetails.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
	                userService.increaseFailedAttempt(userDetails);
	            } else {
	                userService.userAccountLock(userDetails);
	                exception = new LockedException("Account Locked after 3 attempts");
	            }

	        } else {

	            if (userService.unlockAccountTimeExpired(userDetails)) {
	                exception = new LockedException("Account Unlocked! Please login");
	            } else {
	                exception = new LockedException("Your account is locked");
	            }
	        }

	    } else {
	        exception = new LockedException("Your account is inactive");
	    }

	    // ✅ IMPORTANT: redirect URL
	    setDefaultFailureUrl("/signin?error");

	    super.onAuthenticationFailure(request, response, exception);
	}
}
