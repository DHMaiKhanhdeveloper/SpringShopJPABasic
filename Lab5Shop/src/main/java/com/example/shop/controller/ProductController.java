package com.example.shop.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.shop.model.Category;
import com.example.shop.model.Product;
import com.example.shop.respository.CategoryRepository;
import com.example.shop.respository.ProductRepository;

@Controller
@RequestMapping("products")
public class ProductController {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	
	// trả về danh sách Category hiện có trong CSDL
	@ModelAttribute("categories")
	public List<Category> getCategories(){
		return categoryRepository.findAll();
	}
	// Tạo và chỉnh sửa chung 1 hàm 
	@GetMapping(value= {"newOrEdit", "newOrEdit/{id}"})
	public String newOrEdit (ModelMap model,@PathVariable(name = "id", required = false) Optional<Long> id) {
		Product product;
		if (id.isPresent()) {
		Optional<Product> existed =productRepository.findById(id.get());
		product = existed.isPresent()? existed.get(): new Product();
		}else {
			product = new Product();
		}
		
		model.addAttribute("product", product);
		return "products/newOrEdit";
	}

	
	@PostMapping("saveOrUpdate")
	public String saveOrUpdate (RedirectAttributes attributes, ModelMap model,
	Product product) { // điền thông tin sản phầm Product ở trên newOrEdit
		//thì sẽ lấy thông tin Product đã được điền vào hàm saveOrUpdate truyền biến Product product
		productRepository.save(product); // lưu vào CSDL 
		model.addAttribute("product", product);
		attributes.addAttribute("message", "New product is saved!");
		return "redirect:/products";
	}
	
	@GetMapping
	public String list (ModelMap model, @RequestParam Optional<String> message,
	@PageableDefault(size = 2, sort = "name", direction = Direction.ASC) Pageable pageable) {
	Page<Product> pages = productRepository.findAll(pageable);
	if (message.isPresent()) {
		model.addAttribute("message", message.get());
	}
	
	List<Sort.Order> sortOrders = pages.getSort().stream().collect (Collectors.toList());
	if (sortOrders.size() > 0) {
		Sort.Order order = sortOrders.get(0);
		
		model.addAttribute("sort", order.getProperty() + ","+
	(order.getDirection() == Sort.Direction.ASC? "asc": "desc"));
		
	}else {
		// chưa có vị trí sắp xếp nào thì sắp xếp mặc định theo tên và tăng dần 
		model.addAttribute("sort", "name");
	}
	
	model.addAttribute("pages", pages);
	return "products/list";
	}
	
	@GetMapping("delete/{id}")
	public String delete (RedirectAttributes attributes,@PathVariable("id") Long id) {
		productRepository.deleteById(id);
		attributes.addAttribute("message", "The product is deleted!");
		return "redirect:/products";
	}
}
