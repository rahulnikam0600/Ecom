package com.ecom.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;
import com.ecom.service.CommonService;

import jakarta.servlet.http.HttpSession;

@Service
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	CommonService commonService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Override
	public Category saveCategory(Category category, MultipartFile file, HttpSession session) throws IOException {
		
		String imageName = file.isEmpty() ? "default.jpg": file.getOriginalFilename();
		category.setImageName(imageName);
		Category savedCategory = null;
		if(isCategoryExist(category.getName())) {
			session.setAttribute("errorMsg", "Category Already Exists");
		}
		else {
			savedCategory = categoryRepository.save(category);
			if (ObjectUtils.isEmpty(savedCategory)) {
				session.setAttribute("errorMsg", "Not saved ! Internal Server Error");
			}
			else  if ( !file.isEmpty() ) {
				String name =  commonService.saveImage(file, "category_img");
				
				if(!ObjectUtils.isEmpty(name)) {
					session.setAttribute("succMsg", "Saved successfully");
				}
				else {
					session.setAttribute("errorMsg", "Category Created Without Image !");
				}
			}
			else {
				session.setAttribute("succMsg", "Category Created Without Image !");
			}
		}
		return savedCategory;
	}

	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	@Override
	public boolean isCategoryExist(String name) {
		// TODO Auto-generated method stub
		return categoryRepository.existsByName(name);
	}

	@Override
	public boolean deleteCategory(int id) {

		Category category = categoryRepository.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(category)) {
			categoryRepository.delete(category);
			return true;
		}
		
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		return category;
	}
	
	@Override
	public List<Category> getAllActiveCategory() {
		List<Category> categories = categoryRepository.findByIsActiveTrue();
		return categories;
	}
}
