package com.eshop.admin.product;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.eshop.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

}
