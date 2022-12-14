package com.eshop.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;

@RestController
public class BrandRestController {
	@Autowired BrandService brandService;
	
	@PostMapping("/brands/check_unique")
	public String checkUnique(Integer id, String name) {
		return brandService.checkUniqueName(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
		List<CategoryDTO> listCategoriesByBrand = new ArrayList<>();
		
		try {
			Brand brand = brandService.get(brandId);
			Set<Category> categories = brand.getCategories();
			
			for (Category category : categories) {
				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
				listCategoriesByBrand.add(dto);
			}
			
			return listCategoriesByBrand;
		} catch (BrandNotFoudException e) {
			throw new BrandNotFoundRestException();
		}
	}
	
}
