package com.cartsafari.service.imp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cartsafari.model.Cart;
import com.cartsafari.model.Product;
import com.cartsafari.model.UserDetails;
import com.cartsafari.repository.CartRepository;
import com.cartsafari.repository.ProductRepository;
import com.cartsafari.repository.UserRepository;
import com.cartsafari.service.CartService;

@Service
public class CartServiceImp implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		UserDetails userDetails = userRepository.findById(userId).get();
		Product product = productRepository.findById(productId).get();

		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

		Cart cart = null;

		if (ObjectUtils.isEmpty(cartStatus)) {
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userDetails);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * product.getDiscountPrice());
		} else {
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
		}

		Cart saveCart = cartRepository.save(cart);

		return saveCart;
	}

	@Override
	public List<Cart> getCartByUser(Integer userId) {
		List<Cart> carts = cartRepository.findByUserId(userId);
		Double totalOrderPrice = 0.0;
		List<Cart> updateCart = new ArrayList<>();

		for (Cart c : carts) {
			Double totalPrice = c.getProduct().getDiscountPrice() * c.getQuantity();

			c.setTotalPrice(totalPrice); // ✅ each item total

			totalOrderPrice += totalPrice; // ✅ correct sum
			c.setTotalOrderPrice(totalOrderPrice);
			updateCart.add(c);
		}

		return updateCart;
	}

	@Override
	public Integer getCountCart(Integer userId) {

		Integer countByUserId = cartRepository.countByUserId(userId);

		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;
		if (sy.equalsIgnoreCase("minus")) {
			updateQuantity = cart.getQuantity() - 1;
			if (updateQuantity <= 0) {
				cartRepository.delete(cart);;	
			}else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}
		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}
		
		
	}

}
