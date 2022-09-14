package com.eshop.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.FileUploadUtil;
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

	@PostMapping("/categories/save")
	public String saveCategory(Category category,
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImage(fileName);

		Category savedCategory = categoryService.save(category);
		String uploadDir = "../category-images/" + savedCategory.getImage();
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
		return "redirect:/categories";
	}
}
