package com.eshop.admin.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.common.entity.Category;

@Controller
public class CategoryController {
	@Autowired CategoryService categoryService;
	
	@GetMapping("/categories")
	public String listAll(Model model) {
		List<Category> listCategories = categoryService.listAll();
		model.addAttribute("listCategories", listCategories);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create new category");
		
		return "categories/category_form";
	}
}