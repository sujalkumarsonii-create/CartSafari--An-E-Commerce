package com.cartsafari.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

import com.cartsafari.model.Category;
import com.cartsafari.model.Product;
import com.cartsafari.model.UserDetails;
import com.cartsafari.service.CartService;
import com.cartsafari.service.CategoryService;
import com.cartsafari.service.ProductService;
import com.cartsafari.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@ModelAttribute
	public void getUserDetails(Principal principal,Model m) {
		if(principal!=null) {
			String name = principal.getName();
			UserDetails userByEmail = userService.getUserByEmail(name);
			m.addAttribute("user", userByEmail);
			Integer countCart = cartService.getCountCart(userByEmail.getId());
			m.addAttribute("countCart",countCart);
		}
		List<Category> allActiveCategories = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategories);
	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/category")
	public String category(Model m) {
		m.addAttribute("categorys", categoryService.getAllCategory());
		return "admin/category";
	}

	/*
	 * @PostMapping("/saveCategory") public String saveCategory(@ModelAttribute
	 * Category category, @RequestParam("file") MultipartFile file, HttpSession
	 * session) throws IOException {
	 * 
	 * String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
	 * 
	 * category.setImageName(imageName);
	 * 
	 * if (categoryService.existCategory(category.getName())) {
	 * session.setAttribute("errmsg", "category name already exist"); } else { if
	 * (ObjectUtils.isEmpty(categoryService.saveCategory(category))) {
	 * session.setAttribute("errmsg", "Internal server error"); } else { File
	 * saveFile = new ClassPathResource("static/img").getFile();
	 * 
	 * Path path =
	 * Paths.get(saveFile.getAbsolutePath()+File.separator+"category"+File.separator
	 * + file.getOriginalFilename()); System.out.println(path);
	 * Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
	 * 
	 * session.setAttribute("success", "saved successfully"); } } return
	 * "redirect:/admin/category"; }
	 */

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = (file != null && !file.isEmpty()) ? file.getOriginalFilename() : "default.jpg";

		category.setImageName(imageName);

		if (categoryService.existCategory(category.getName())) {
			session.setAttribute("errmsg", "Category name already exists");
			return "redirect:/admin/category";
		}

		categoryService.saveCategory(category);

		// ✅ EXTERNAL DIRECTORY (NOT target, NOT static)
		String uploadDir = "C:/cartsafari/uploads/category/";
		Path uploadPath = Paths.get(uploadDir);

		// ✅ ENSURE DIRECTORY EXISTS
		Files.createDirectories(uploadPath);

		// ✅ COPY FILE
		if (file != null && !file.isEmpty()) {
			Path filePath = uploadPath.resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		}

		session.setAttribute("success", "Saved successfully");
		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("success", "category delete successfully");
		} else {
			session.setAttribute("errmsg", "something wrong on server");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		Category oldCategory = categoryService.getCategoryById(category.getId());

		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}
		Category updateCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

			if (!file.isEmpty()) {
				// ✅ EXTERNAL DIRECTORY (NOT target, NOT static)
				String uploadDir = "C:/cartsafari/uploads/category/";
				Path uploadPath = Paths.get(uploadDir);

				// ✅ ENSURE DIRECTORY EXISTS
				Files.createDirectories(uploadPath);

				// ✅ COPY FILE
				if (file != null && !file.isEmpty()) {
					Path filePath = uploadPath.resolve(file.getOriginalFilename());
					Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
			session.setAttribute("success", "category updated successfully");
		} else {
			session.setAttribute("errmsg", "Internal server issue data not updated");
		}
		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@Autowired
	private ProductService productService;

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = (image != null && !image.isEmpty()) ? image.getOriginalFilename() : "default.jpg";

		product.setImage(imageName);

		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());

		Product saveproduct = productService.saveproduct(product);

		if (!ObjectUtils.isEmpty(saveproduct)) {

			if (!image.isEmpty()) {
				// ✅ EXTERNAL DIRECTORY (NOT target, NOT static)
				String uploadDir = "C:/cartsafari/uploads/product/";
				Path uploadPath = Paths.get(uploadDir);

				// ✅ ENSURE DIRECTORY EXISTS
				Files.createDirectories(uploadPath);

				// ✅ COPY FILE
				if (image != null && !image.isEmpty()) {
					Path filePath = uploadPath.resolve(image.getOriginalFilename());
					Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				}
			}

			session.setAttribute("success", "product saved successfully");
		} else {
			session.setAttribute("errmsg", "something wrong on server");
		}
		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/products")
	public String loadViewProduct(Model m) {
		m.addAttribute("products", productService.getAllProduct());
		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("success", "product delete successfully");
		} else {
			session.setAttribute("errmsg", "something wrong on server");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m, HttpSession session) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("category", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {
		Product oldProduct = productService.getProductById(product.getId());

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errmsg", "Invalid Discount");
		} else {
			String imageName = image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();

			if (!ObjectUtils.isEmpty(product)) {
				oldProduct.setTitle(product.getTitle());
				oldProduct.setCategory(product.getCategory());
				oldProduct.setDescription(product.getDescription());
				oldProduct.setPrice(product.getPrice());
				oldProduct.setStock(product.getStock());
				oldProduct.setDiscount(product.getDiscount());
				oldProduct.setDiscountPrice(product.getPrice() - (product.getPrice() * (product.getDiscount() / 100.0)));
				oldProduct.setIsActive(product.getIsActive());
				oldProduct.setImage(imageName);
			}

			Product updateProduct = productService.saveproduct(oldProduct);

			if (!ObjectUtils.isEmpty(updateProduct)) {

				if (!image.isEmpty()) {
					// ✅ EXTERNAL DIRECTORY (NOT target, NOT static)
					String uploadDir = "C:/cartsafari/uploads/product/";
					Path uploadPath = Paths.get(uploadDir);

					// ✅ ENSURE DIRECTORY EXISTS
					Files.createDirectories(uploadPath);

					// ✅ COPY FILE
					if (image != null && !image.isEmpty()) {
						Path filePath = uploadPath.resolve(image.getOriginalFilename());
						Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
					}
				}

				session.setAttribute("success", "product updated successfully");
			} else {
				session.setAttribute("errmsg", "something wrong on server");
			}
		}

		return "redirect:/admin/editProduct/" + product.getId();
	}
	
	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDetails> users = userService.getUsers("ROLE_USER");
		m.addAttribute("users", users);
		return "/admin/users";
	}
	
	
	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,HttpSession session) {
		
		Boolean updateStatus = userService.updateAccountStatus(id,status);
		if(updateStatus) {
			session.setAttribute("success", "Account Status Upadated");
		}else {
			session.setAttribute("errmsg", "something wrong on server");
		}
		return "redirect:/admin/users";
	}

}
