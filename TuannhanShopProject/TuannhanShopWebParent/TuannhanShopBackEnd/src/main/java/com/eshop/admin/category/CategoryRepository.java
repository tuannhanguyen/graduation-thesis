package com.eshop.admin.category;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.eshop.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

}
