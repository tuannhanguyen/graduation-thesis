package com.eshop.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Product;

@Service
public class ProductService {
	@Autowired ProductRepository productRepository;
	
	public List<Product> listAll() {
		return (List<Product>) productRepository.findAll();
	}
}
