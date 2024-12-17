package com.ecom.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;

public interface ProductService {

	public Product saveProduct(Product product);
	
	public List<Product> getAllProducts();
	
	public boolean deleteProduct(Integer id);
	
	public Product getProductById(Integer id);

	Product updateProduct(Product product, MultipartFile image);

	List<Product> getAllActiveProducts(String category);
	
}
