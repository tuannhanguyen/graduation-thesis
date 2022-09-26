package com.eshop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.category.CategoryService;
import com.eshop.common.entity.Category;

@Controller
public class MainController {

	@Autowired CategoryService categoryService;

	@GetMapping("/")
	public String viewHomePage(Model model) {
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);

		return "index";
	}
}
