package com.cartsafari.service.imp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cartsafari.model.Cart;
import com.cartsafari.model.OrderAddress;
import com.cartsafari.model.OrderRequest;
import com.cartsafari.model.ProductOrder;
import com.cartsafari.repository.CartRepository;
import com.cartsafari.repository.ProductOrderRepository;
import com.cartsafari.service.OrderService;
import com.cartsafari.util.OrderStatus;

@Service
public class OrderServiceImp implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	
	
	@Override
	public void saveOrder(Integer userId, OrderRequest orderRequest) {
		
		List<Cart> carts = cartRepository.findByUserId(userId);
		for( Cart cart : carts) {
			ProductOrder order = new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(new Date());
			
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());
			
			OrderAddress address = new OrderAddress();
			address.setFullName(orderRequest.getFullName());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setPincode(orderRequest.getPincode());
			address.setState(orderRequest.getState());
			
			order.setOrderAddress(address);
			
			orderRepository.save(order);
		}
	}

}
