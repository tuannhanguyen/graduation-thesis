package com.eshop.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.user.CategoryPageInfo;
import com.eshop.common.entity.Category;

@Controller
public class CategoryController {
	@Autowired CategoryService categoryService;

	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, sortDir, model);
	}

	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			@Param("sortDir") String sortDir, Model model) {
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}

		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = categoryService.listByPage(pageInfo, pageNum, sortDir);

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("totalPages", pageInfo.getTotalPage());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);

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
		if (!multipartFile.isEmpty()) {
			String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);

			Category savedCategory = categoryService.save(category);
			String uploadDir = "../category-images/" + savedCategory.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			categoryService.save(category);
		}

		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
		return "redirect:/categories";
	}

	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Category category = categoryService.get(id);
			List<Category> listCategories = categoryService.listCategoriesUsedInForm();

			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit category (ID: " + id + ")");

			return "categories/category_form";
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}

	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateEnabled(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled,
			RedirectAttributes redirectAttributes) throws CategoryNotFoundException {
		categoryService.updateStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/categories";
	}

	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) {
		try {
			categoryService.deleteCategory(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);

			redirectAttributes.addFlashAttribute("message", "The category with ID " + id + " has been deleted successfully" );
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/categories";
	}
}
