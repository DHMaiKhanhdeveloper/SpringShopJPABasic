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
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.shop.model.Category;
import com.example.shop.respository.CategoryRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("categories")
public class CategoryController {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	//@RequestParam : để chấp nhận một tham số tùy chọn từ URL.
	@GetMapping
	public String list (ModelMap model, @RequestParam Optional<String> message) {
	Iterable<Category> list = categoryRepository.findAll();
	//isPresent(): Kiểm tra xem Optional có chứa giá trị hay không.
	if (message.isPresent()) {
		model.addAttribute("message", message.get());
		}
		
		model.addAttribute("categories", list);
		return "categories/list";
	}
	
	@GetMapping("sort")
	public String sort (ModelMap model, @RequestParam Optional<String> message,
	@SortDefault(sort = "name", direction=Direction.ASC) Sort sort) {
	
	Iterable<Category> list = categoryRepository.findAll(sort);
	if (message.isPresent()) {
		model.addAttribute("message", message.get());
		}
	
		model.addAttribute("categories", list);
		return "categories/sort";
	}
	
	
	
	
//	@GetMapping(value= {"newOrEdit"})
//	public String newOrEdit(ModelMap model) {
//		
//		Category category = new  Category();
//		model.addAttribute("category", category);
//		return "categories/newOrEdit";
//	}
	@GetMapping(value= {"newOrEdit", "newOrEdit/{id}"})
	public String newOrEdit (ModelMap model,
			@PathVariable(name = "id", required = false) Optional<Long> id) {
		Category category;
		if (id.isPresent()) { //Kiểm tra xem biến id có tồn tại giá trị hay không => Category đã tồn tại trong cơ sở dữ liệu.
			Optional<Category> existedCate = categoryRepository.findById(id.get());
			//Thực hiện truy vấn CSDL để lấy thông tin của đối tượng Category dựa trên giá trị của id.
			category = existedCate.isPresent() ?existedCate.get(): new Category();
			//Nếu đối tượng Category có tồn tại trong cơ sở dữ liệu (tức là existedCate.isPresent() trả về true), chúng ta lấy đối tượng Category đó từ existedCate.
			//Nếu không, chúng ta tạo ra một đối tượng Category mới để cho người dùng nhập thông tin.
		}else { 
			category= new Category();
			// Nếu biến id không tồn tại (không có trong URL), chúng ta tạo một đối tượng Category mới để cho người dùng nhập thông tin.
			}
			
		model.addAttribute("category", category);
		//Chúng ta thêm đối tượng Category vào ModelMap để chuyển nó tới giao diện người dùng.
		return "categories/newOrEdit";
	}
	@GetMapping("paginate")
	public String paginate (ModelMap model, @RequestParam Optional<String> message,
			//Sắp xếp mặc định tên tên gồm 2 phần tử , sắp xếp tăng dần
			@PageableDefault (size=2, sort="name", direction =Direction.ASC) Pageable pageable) {
		Page<Category> pages= categoryRepository.findAll(pageable);
			if (message.isPresent()) {
				model.addAttribute("message", message.get());
			}
			
			//kiểm tra xem trang pages có chứa yêu cầu sắp xếp không.
			//pages.getSort(): Đây là cách để lấy thông tin về yêu cầu sắp xếp từ đối tượng Page. getSort() trả về đối tượng Sort chứa các thông tin về sắp xếp
			//stream(): Chuyển đổi đối tượng Sort thành một luồng dữ liệu (Stream) của các yêu cầu sắp xếp (Sort.Order).
			//collect(Collectors.toList()): Thực hiện thu thập các yêu cầu sắp xếp từ luồng và lưu chúng vào danh sách (List) các đối tượng Sort.Order.
			//collect(Collectors.toList()) chuyển Sort Sang List<Sort>
			List<Sort.Order> sortOrders = pages.getSort().stream().collect (Collectors.toList());
			if (sortOrders.size() > 0) {//Kiểm tra xem danh sách sortOrders có phần tử nào không. 
				//lấy vị trí sắp xếp đầu tiên
				Sort.Order order = sortOrders.get(0);
				// Lý do cần lấy thứ tự sắp xếp đầu tiên là để hiển thị thông tin về cách sắp xếp dữ liệu trang trên giao diện người dùng.
				model.addAttribute("sort", order.getProperty() + ","
				+ (order.getDirection() == Sort.Direction.ASC?"asc": "desc"));
				//Chúng ta thêm thông tin về sắp xếp vào ModelMap. Nếu thứ tự là tăng dần (ASC), chúng ta đặt giá trị asc, nếu thứ tự là giảm dần (DESC), chúng ta đặt giá trị desc.
			}else {
				// danh sách sortOrders không có phần tử nào?
				// chưa có vị trí sắp xếp nào thì sắp xếp mặc định theo tên và tăng dần 
				model.addAttribute("sort", "name");
			}
			
			model.addAttribute("pages", pages);
			return "categories/paginate";
	}
	
//	@PostMapping("saveOrUpdate")
//	public String saveOrUpdate(RedirectAttributes attributes,Category item) {
//		categoryRepository.save(item);
//		attributes.addAttribute("message", "New category is saved!");
//		return "redirect:/categories";
//	}
	
	@PostMapping("saveOrUpdate")
	public String saveOrUpdate (RedirectAttributes attributes, ModelMap model,
	//  Category item thông tin Category do người dùng nhập vào trên form
	@Valid Category item, BindingResult result) {//BindingResult => validate kiểm tra dữ liệu có hợp lệ không ?
		if (result.hasErrors()){ // kiểm tra có lỗi
			model.addAttribute("category", item);
			//Đưa đối tượng Category mà người dùng đã nhập vào model để có thể hiển thị lại trên trang và người dùng  cần nhập lại.
			return "categories/newOrEdit";
			// trả lại tran ban đầu dể người dùng nhập lại khi nhập sai
		}
		//Nếu không có lỗi
		categoryRepository.save(item);
		//Lưu đối tượng Category vào cơ sở dữ liệu thông qua categoryRepository.
		attributes.addAttribute("message", "New category is saved!");
		//hêm thông điệp "New category is saved!" vào các thuộc tính của URL để chuyển đến trang mới.
		return "redirect:/categories";
		//Khi mọi thứ hoàn thành mà không có lỗi, chương trình sẽ chuyển hướng người dùng đến trang "categories" 
	}
	@GetMapping("delete/{id}")
	public String delete (RedirectAttributes attributes,
		@PathVariable("id") Long id) {
		categoryRepository.deleteById(id);
		attributes.addAttribute("message", "The category is deleted!");
		return "redirect:/categories";
	}
}
