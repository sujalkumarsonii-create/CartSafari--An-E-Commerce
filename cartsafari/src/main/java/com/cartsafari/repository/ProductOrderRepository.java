package com.cartsafari.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartsafari.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
	
	public List<ProductOrder> findByUserId(Integer userId);
	
	

}
