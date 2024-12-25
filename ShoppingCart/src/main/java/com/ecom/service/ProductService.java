package com.ecom.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;

public interface ProductService {

	public Boolean saveProduct(Product product, MultipartFile file);
	
	public Boolean updateProduct(Product product, MultipartFile file);

	public boolean deleteProduct(Integer id);
	
	public List<Product> getAllProducts();
	
	List<Product> getAllActiveProducts(String category);

	public Product getProductById(Integer id);
}
