package com.eshop.admin.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Category;

@Service
public class CategoryService {
	@Autowired CategoryRepository categoryRepository;
	
	public List<Category> listAll() {
		return (List<Category>) categoryRepository.findAll();
	}
	
}
