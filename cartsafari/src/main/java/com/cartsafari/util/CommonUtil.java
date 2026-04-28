package com.cartsafari.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	public Boolean sendMail(String url, String email) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom("sujalkumarsoniofficial@gmail.com", "CartSafari");
		helper.setTo(email);
		String content = "<p>Hello [First Name],</p>" 
				+ "<p>We received a request to reset the password for your account.</p>"
				+ "<p>If you made this request, please click the link below to set a new password:</p>"
				+ "<p><a href=\"" +url + "\">Change me Password...</a></p>" + "<p>For your security, this link will expire in [X hours].</p>"
				+ "<p>If you did not request a password reset, please ignore this email or contact our <br>support team immediately at [Support Email/Phone].</p>"
				+ "<p>Thank you," + "<h2>[Your Company Name]</h2>" + "<br>[Company Contact Info]";

		helper.setSubject("Password Reset Request");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		String sentUrl = request.getRequestURL().toString();
		

		return sentUrl.replace(request.getServletPath(), "");
	}
}
