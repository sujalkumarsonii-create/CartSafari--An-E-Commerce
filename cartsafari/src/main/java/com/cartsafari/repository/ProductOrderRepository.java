package com.cartsafari.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartsafari.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

}
