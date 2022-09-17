package com.eshop.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {
	@Autowired BrandService brandService;
	
	@PostMapping("/brands/check_unique")
	public String checkUnique(Integer id, String name) {
		return brandService.checkUniqueName(id, name);
	}
}
