package com.cartsafari.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cartsafari.model.Cart;
import com.cartsafari.model.Category;
import com.cartsafari.model.OrderRequest;
import com.cartsafari.model.ProductOrder;
import com.cartsafari.model.UserDetails;
import com.cartsafari.service.CartService;
import com.cartsafari.service.CategoryService;
import com.cartsafari.service.OrderService;
import com.cartsafari.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal principal, Model m) {
		if (principal != null) {
			String name = principal.getName();
			UserDetails userByEmail = userService.getUserByEmail(name);
			m.addAttribute("user", userByEmail);
			Integer countCart = cartService.getCountCart(userByEmail.getId());
			m.addAttribute("countCart", countCart);
		}
		List<Category> allActiveCategories = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategories);
	}

	@GetMapping("/addToCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {

		Cart saveCart = cartService.saveCart(pid, uid);
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errmsg", "Failed to Add to Cart");
		} else {
			session.setAttribute("success", "Added to Cart");
		}
		return "redirect:/product/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		UserDetails user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	private UserDetails getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDetails userByEmail = userService.getUserByEmail(email);
		return userByEmail;
	}
	
	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		UserDetails user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			Integer quantity = 0;
			for(Cart c : carts) {
				if (c.getQuantity() != null) {
			        quantity += c.getQuantity();
			    }
			}
			m.addAttribute("totalOrderPrice", totalOrderPrice);
			m.addAttribute("quantity", quantity);
		}
		
		return "/user/order";
	}
	
	
	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) {
		UserDetails user = getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);
		System.out.println(request);
		return "redirect:/user/success";
	}
	
	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}
	
	@GetMapping("/user-orders")
	public String myOrder(Principal p,Model m) {
		UserDetails loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		m.addAttribute("orders",orders);
		return "/user/my_orders";
	}
	
	@GetMapping("/view_order/{id}")
	public String getOrderDetails(@PathVariable Integer id, Principal p, Model m) {
		
		UserDetails loginUser = getLoggedInUserDetails(p);
	    Optional<ProductOrder> order = orderService.findByProductId(id,loginUser.getId()); 
	    if (order == null) {
	        return "redirect:/user/user-orders";
	    }
	    m.addAttribute("order", order);

	    return "/user/view_order";
	}
}
