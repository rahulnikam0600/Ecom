package com.ecom.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;

public interface CategoryService {
	
	public Boolean saveCategory(Category category, MultipartFile file) throws IOException;

	public Boolean updateCategory(Category category, MultipartFile file) throws IOException;

	public boolean isCategoryExist(String name);
	
	public List<Category> getAllCategory();
	
	public boolean deleteCategory(int id);
	
	public Category getCategoryById(int id);

	List<Category> getAllActiveCategory();

}
