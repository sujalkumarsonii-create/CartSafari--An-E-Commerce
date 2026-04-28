package com.cartsafari.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cartsafari.model.Product;
import com.cartsafari.repository.ProductRepository;
import com.cartsafari.service.ProductService;

@Service
public class ProductServiceImp implements ProductService{

	@Autowired
	private ProductRepository productRepository;
	
	
	@Override
	public Product saveproduct(Product product) {
		
		return productRepository.save(product);
	}


	@Override
	public List<Product> getAllProduct() {
		
		return productRepository.findAll();
	}


	@Override
	public Boolean deleteProduct(Integer id) {
		
		if(!ObjectUtils.isEmpty(productRepository.findById(id).orElse(null))){
			productRepository.delete(productRepository.findById(id).orElse(null));
			return true;
		}
		return false;
	}
	
	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}


	@Override
	public List<Product> getAllActiveProduct(String category) {
		
		List<Product> products;
		
		if(ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		}else {
			products = productRepository.findByCategory(category);
		}
		return products;
	}

}
