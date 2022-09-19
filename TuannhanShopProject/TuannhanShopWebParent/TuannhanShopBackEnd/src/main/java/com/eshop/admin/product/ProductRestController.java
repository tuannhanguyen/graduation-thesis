package com.eshop.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {
	@Autowired private ProductService productService;

	@PostMapping("/products/check_unique")
	public String checkUnique(Integer id, String name) {
		return productService.checkUnique(id, name);
	}
}
