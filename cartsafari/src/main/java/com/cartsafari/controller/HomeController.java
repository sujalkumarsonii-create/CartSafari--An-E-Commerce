package com.cartsafari.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cartsafari.model.Category;
import com.cartsafari.model.Product;
import com.cartsafari.model.UserDetails;
import com.cartsafari.service.CartService;
import com.cartsafari.service.CategoryService;
import com.cartsafari.service.ProductService;
import com.cartsafari.service.UserService;
import com.cartsafari.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CartService cartService;
	
	@ModelAttribute
	public void getUserDetails(Principal principal,Model m) {
		if(principal!=null) {
			String name = principal.getName();
			UserDetails userByEmail = userService.getUserByEmail(name);
			m.addAttribute("user", userByEmail);
			Integer countCart = cartService.getCountCart(userByEmail.getId());
			m.addAttribute("countCart",countCart);
		}
		List<Category> allActiveCategories = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategories);
	}
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/products")
	public String products(Model m,@RequestParam(value = "category",defaultValue = "") String category) {
		
		List<Category> categories = categoryService.getAllActiveCategory();
		
		List<Product> products = productService.getAllActiveProduct(category);
		
		m.addAttribute("categories",categories);
		m.addAttribute("products",products);
		m.addAttribute("paramValue",category);
		
		return "product";
	}
	
	@GetMapping("/product/{id}")
	public String product(@PathVariable int id,Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product",productById);
		return "view_product";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDetails user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException
	{
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfilePicture(imageName);
		UserDetails saveUser = userService.saveUser(user);
		
		if(!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				// ✅ EXTERNAL DIRECTORY (NOT target, NOT static)
				String uploadDir = "C:/cartsafari/uploads/profile/";
				Path uploadPath = Paths.get(uploadDir);

				// ✅ ENSURE DIRECTORY EXISTS
				Files.createDirectories(uploadPath);

				// ✅ COPY FILE
				if (file != null && !file.isEmpty()) {
					Path filePath = uploadPath.resolve(file.getOriginalFilename());
					Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				}	
			}
			session.setAttribute("success", "profile registered successfully");
		}
		else {
			session.setAttribute("errmsg", " profile not registered");
		}
		
		return "redirect:/register";
	}
	
	
	// API's For Forgot Password
	
	@GetMapping("/forgot_password")
	public String showForgotPasswordPage() {
		return "forgot_password";
	}
	
	@PostMapping("/forgot_password")
	public String processForgotPasswordPage(@RequestParam String email,HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		UserDetails userByEmail = userService.getUserByEmail(email);
		if(ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errmsg", "Invalid Email");
		}else {
			
			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);
			
			// Url Create
			
			String url = CommonUtil.generateUrl(request)+"/reset_password?token="+resetToken;
			
			
			Boolean sendMail = commonUtil.sendMail(url,email);
			if(sendMail) {
				session.setAttribute("success", "reset link sent on registered E-mail");
			}else {
				session.setAttribute("errmsg", "something went wrong...reset link not sent");
			}
		}
		return "redirect:/forgot_password";
	}
	
	@GetMapping("/reset_password")
	public String showResetPasswordPage(@RequestParam String token,HttpSession session,Model m) {
		UserDetails userByToken = userService.getUserByToken(token);
		
		if(ObjectUtils.isEmpty(userByToken)) {
			m.addAttribute("msg","Your link is Invalid/Expired");
			return "password_message";
		}
		m.addAttribute("token",token);
		return "reset_password";
	}
	
	@PostMapping("/reset_password")
	public String resetPasswordPage(@RequestParam String token,@RequestParam String password,HttpSession session,Model m) {
		UserDetails userByToken = userService.getUserByToken(token);
		
		if(ObjectUtils.isEmpty(userByToken)) {
			m.addAttribute("msg","Your link is Invalid/Expired");
			return "password_message";
		}else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			session.setAttribute("success","Password Change successfully");
			m.addAttribute("msg","Password Change successfully");
			return "password_message";
		}
		
		
	}
	
}
