package com.cartsafari.service;

import java.util.List;
import java.util.Optional;

import com.cartsafari.model.OrderRequest;
import com.cartsafari.model.ProductOrder;

public interface OrderService {

	public void saveOrder(Integer userId, OrderRequest orderRequest);
	
	public List<ProductOrder> getOrdersByUser(Integer userId);	
	
	public Optional<ProductOrder> findByProductId(Integer productOrderId, Integer userId);
	 
	
	
}
