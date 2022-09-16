package com.eshop.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.category.CategoryService;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;

@Controller
public class BrandController {
	@Autowired BrandService brandService;
	@Autowired CategoryService categoryService;

	@GetMapping("/brands")
	public String listAll(Model model) {
		List<Brand> listBrands = brandService.listAll();
		model.addAttribute("listBrands", listBrands);

		return "brands/brands";
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();

		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create new brand");

		return "brands/brand_form";
	}

	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);

			Brand savedBrand = brandService.save(brand);
			String uploadDir = "../brand-logos" + savedBrand.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			brandService.save(brand);
		}
		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
		return "redirect:/brands";
	}

	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes redirectAttributes) {
		try {
			Brand brand = brandService.get(id);

			List<Category> listCategories = categoryService.listCategoriesUsedInForm();

			model.addAttribute("brand", brand);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit brand (ID: " + id + ")");

			return "brands/brand_form";
		} catch (BrandNotFoudException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}

	@GetMapping("/brands/delete/{id}")
	public String deleteBarnd(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes redirectAttributes) {
		try {
			brandService.delete(id);
			String brandDir = "../brand-logos/" + id;
			FileUploadUtil.removeDir(brandDir);

			redirectAttributes.addFlashAttribute("message", "The brand ID " + id +" has been deleted successfully");
		} catch (BrandNotFoudException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/brands";
	}

}
