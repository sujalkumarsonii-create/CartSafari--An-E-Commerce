package com.cartsafari.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartsafari.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

	public List<Product> findByIsActiveTrue();

	public List<Product> findByCategory(String category);

}
