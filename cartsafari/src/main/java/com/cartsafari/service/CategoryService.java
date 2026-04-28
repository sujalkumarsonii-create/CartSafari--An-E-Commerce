package com.cartsafari.service;

import java.util.List;

import com.cartsafari.model.Category;

public interface CategoryService {

	public Category saveCategory(Category category);
	
	public List<Category> getAllCategory();
	
	public Boolean existCategory(String Name);
	
	public Boolean deleteCategory(int id);
	
	public Category getCategoryById(int id);
	
	public List<Category> getAllActiveCategory();
}
