package com.ecom.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;
	
//*********************************************************************************************************************************
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDtls> users = userService.getUsers("ROLE_USER");
		m.addAttribute("users", users);
		return "/admin/users";
	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "Something Wrong On Server!");
		}
		return "redirect:/admin/users";
	}

//*********************************************************************************************************************************
	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/add_product";
	}

//*********************************************************************************************************************************
	@GetMapping("/category")
	public String category(Model m) {
		m.addAttribute("categorys", categoryService.getAllCategory());
		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String addCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
				
		if(categoryService.isCategoryExist(category.getName())) {
			session.setAttribute("errorMsg", "Category Already Exists");
		}else {
			Boolean saved =  categoryService.saveCategory(category, file);
			if(saved) {
				session.setAttribute("succMsg", "Saved successfully");
			}else {
				session.setAttribute("errorMsg", "Not saved ! Internal Server Error");
			}
		}
		
		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		
		Boolean deleted = categoryService.deleteCategory(id);
		if (deleted) {
			session.setAttribute("succMsg", "Category Deleted Successfully!");
		} else {
			session.setAttribute("errorMsg", "Something Wrong On Server");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String editCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		Boolean updated = categoryService.updateCategory(category, file);
		if(updated) {
			session.setAttribute("succMsg", "Category Updated Sucessfully!");
		}else {
			session.setAttribute("errorMsg", "Not Updated! Internal Server Error!");
		}
		
		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

//*********************************************************************************************************************************
	@PostMapping("/saveProduct")
	public String addProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		System.err.println(product.getTitle());
		
		if(ObjectUtils.isEmpty(product.getTitle())) {
			System.err.println("****************************************************************************************************");
		}
		
		Boolean saved = productService.saveProduct(product, file);
		if (saved) {
			session.setAttribute("succMsg", "Product Saved Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Wrong On Server!");
		}

		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/products")
	public String loadViewProduct(Model m) {
		m.addAttribute("products", productService.getAllProducts());
		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleted = productService.deleteProduct(id);
		if (deleted) {
			session.setAttribute("succMsg", "Product Delete Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Wrong On Server!");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Invalid Discount");
		} else {
			Boolean updated = productService.updateProduct(product, file);
			if (updated) {
				session.setAttribute("succMsg", "Product Update Successfully!");
			} else {
				session.setAttribute("errorMsg", "Something Wrong On Server!");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}

}