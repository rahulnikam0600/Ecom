package com.ecom.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;
import com.ecom.service.CommonService;

@Service
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Override
	public Boolean saveCategory(Category category, MultipartFile file) throws IOException {
		
		String imageName = file.isEmpty() ? "default.jpg": file.getOriginalFilename();
		category.setImageName(imageName);
		Boolean saved = false;
		Category savedCategory = categoryRepository.save(category);
		if (!ObjectUtils.isEmpty(savedCategory)) {
				commonService.saveImage(file, "category_img");
			saved = true;
		}
		return saved;
	}

	@Override
	public Boolean updateCategory(Category category, MultipartFile file) throws IOException {
		
		Boolean updated = false;
		Category oldCategory = getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();
		
		if(!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}
		
		Category updatedCategory = categoryRepository.save(oldCategory);
		
		if (!ObjectUtils.isEmpty(updatedCategory)) {
			commonService.saveImage(file, "category_img");
			updated = true;
		}
		return updated;
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
