package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CommonService;
import com.ecom.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private final String folderName = "product_img";

	@Autowired
	 private ProductRepository productRepository;
	
	@Autowired
	private CommonService commonService;
		
	@Override
	public Boolean saveProduct(Product product, MultipartFile file) {
		
		Boolean saved = false;
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product savedProduct = productRepository.save(product);
		
		if(!ObjectUtils.isEmpty(savedProduct)) {
			commonService.saveImage(file, folderName);
			saved = true;
		}
		return saved;
	}

	@Override
	public Boolean updateProduct(Product product, MultipartFile file) {

		Boolean updated = false;
		Product dbProduct = getProductById(product.getId());

		String imageName = file.isEmpty() ? dbProduct.getImage() : file.getOriginalFilename();

		dbProduct.setTitle(product.getTitle());
		dbProduct.setDescription(product.getDescription());
		dbProduct.setCategory(product.getCategory());
		dbProduct.setPrice(product.getPrice());
		dbProduct.setStock(product.getStock());
		dbProduct.setImage(imageName);
		dbProduct.setIsActive(product.getIsActive());
		dbProduct.setDiscount(product.getDiscount());

		// 5=100*(5/100); 100-5=95
		Double disocunt = product.getPrice() * (product.getDiscount() / 100.0);
		Double discountPrice = product.getPrice() - disocunt;
		dbProduct.setDiscountPrice(discountPrice);

		Product updatedProduct = productRepository.save(dbProduct);

		if (!ObjectUtils.isEmpty(updatedProduct)) {
			commonService.saveImage(file, folderName);
			updated = true;
		}
		return updated;
	}

	@Override
	public boolean deleteProduct(Integer id) {
		
		Product product = productRepository.findById(id).orElse(null);
		
		if (!ObjectUtils.isEmpty(product)) {
			productRepository.deleteById(id);
			return true;
		}
		return false;
	}
	
	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = null;
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		}else {
			products=productRepository.findByCategory(category);
		}

		return products;
	}

	@Override
	public Product getProductById(Integer id) {
		return productRepository.findById(id).get();
	}

}
