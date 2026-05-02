package com.cartsafari.service;

import com.cartsafari.model.OrderRequest;

public interface OrderService {

	public void saveOrder(Integer userId, OrderRequest orderRequest);
}
