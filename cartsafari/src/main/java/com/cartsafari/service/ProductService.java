package com.cartsafari.service;

import java.util.List;

import com.cartsafari.model.Product;

public interface ProductService {
	
	public Product saveproduct(Product product);
	
	public List<Product> getAllProduct();
	
	public Boolean deleteProduct(Integer id);
	
	public Product getProductById(Integer id);
	
	public List<Product> getAllActiveProduct(String category);

}
