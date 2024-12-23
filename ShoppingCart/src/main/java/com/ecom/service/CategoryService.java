package com.ecom.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;

import jakarta.servlet.http.HttpSession;

public interface CategoryService {
	
	public Category saveCategory(Category category, MultipartFile file,HttpSession session) throws IOException;

	public boolean isCategoryExist(String name);
	
	public List<Category> getAllCategory();
	
	public boolean deleteCategory(int id);
	
	public Category getCategoryById(int id);

	List<Category> getAllActiveCategory();

}
