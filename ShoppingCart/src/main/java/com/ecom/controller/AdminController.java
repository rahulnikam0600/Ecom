package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/add-product")
	public String addProduct(Model m) {
		
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories",categories);
		return "admin/add-product";
	}

	@GetMapping("/category")
	public String addCategory(Model m) {
		
		m.addAttribute("categories",categoryService.getAllCategory());
		return "admin/category";
	}

/******************************************************************************************************/	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category,
			@RequestParam("file") MultipartFile file,
			HttpSession session,Model m) throws IOException {
		
		String imageName = (file != null ? file.getOriginalFilename() : "default.jpg");
		
		category.setImageName(imageName);
		
		boolean existCategory = categoryService.existCategory(category.getName());
		
		if(existCategory) {
			session.setAttribute("errorMsg","Category already exist's");
		}
		else {
			Category saveCategory = categoryService.saveCategory(category);
			if(ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved! Internal Server Error");
			}
			else {
				
				File file2 = new ClassPathResource("static/img").getFile();
				
				Path path =  Paths.get(file2.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
				
				//System.out.println(path);
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				session.setAttribute("successMsg", "Saved sucessfully!");
			}
		}

		return "redirect:/admin/category";
	}
	
	/******************************************************************************************************/	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id,
			HttpSession session) {
		
		Boolean deleteCategory = categoryService.deleteCategory(id);
		
		if(deleteCategory) {
			session.setAttribute("successMsg","Category Deleted Successfully");
		}else {
			session.setAttribute("errorMsg","Something Wrong on Server");
		}
		
		return "redirect:/admin/category";
		
	}
	
	/******************************************************************************************************/	
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		
		m.addAttribute("category",categoryService.getCategoryById(id));
		
		return "/admin/edit_category";
	}
	
	/******************************************************************************************************/	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category,
			@RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		
		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ?  oldCategory.getImageName():file.getOriginalFilename();
		
		if(!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}
		
		Category category2 =  categoryService.saveCategory(oldCategory);
		
		if(!ObjectUtils.isEmpty(category2)) {
			
			if(!file.isEmpty()) {
				File file2 = new ClassPathResource("static/img").getFile();
				
				Path path =  Paths.get(file2.getAbsolutePath()+File.separator+"category_img"+File.separator
						+file.getOriginalFilename());
								
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}
			session.setAttribute("successMsg","Category Updated Successfully");
		}else {
			session.setAttribute("errorMsg","Something Wrong on Server");
		}		
		return "redirect:/admin/loadEditCategory/"+category.getId();		
	}
	
	/******************************************************************************************************/
	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product,
			@RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountedPrice(product.getPrice());
		
		
		Product savedProduct =  productService.saveProduct(product);
		
//		System.out.println(savedProduct);
		
		if(!ObjectUtils.isEmpty(savedProduct)) {

			File file2 = new ClassPathResource("static/img").getFile();
			
			Path path =  Paths.get(file2.getAbsolutePath()+File.separator+"product_img"+File.separator
					+file.getOriginalFilename());
			
//			System.out.println(path);
							
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			
			session.setAttribute("successMsg", "Product Saved Successfully");
		}else {
			session.setAttribute("errorMsg", "Something Wrong On Server");
		}
		
		return "redirect:/admin/add-product";
	}

	/******************************************************************************************************/
	@GetMapping("/products")
	public String loadViewProduct(Model model) {
		model.addAttribute("products",productService.getAllProducts());
		return "admin/products";
	}
	
	/******************************************************************************************************/
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable Integer id, HttpSession session) {
		
		Boolean deleted = productService.deleteProduct(id);
		
		if(deleted) {
			session.setAttribute("successMsg","Category Deleted Successfully");
		}else {
			session.setAttribute("errorMsg","Something Wrong on Server");
		}
		
		return "redirect:/admin/products";
	}

	/******************************************************************************************************/
	@GetMapping("/loadEditProduct/{id}")
	public String loadEditProduct(@PathVariable Integer id, Model m) {
		
		m.addAttribute("product",productService.getProductById(id));
		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories",categories);
		
		return "/admin/edit_product";
	}
	
	/******************************************************************************************************/	
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) {

		
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("successMsg", "Product update success");
			} else {
				session.setAttribute("errorMsg", "Something wrong on server");
			}
		}
		return "redirect:/admin/loadEditProduct/" + product.getId();
	}

}
