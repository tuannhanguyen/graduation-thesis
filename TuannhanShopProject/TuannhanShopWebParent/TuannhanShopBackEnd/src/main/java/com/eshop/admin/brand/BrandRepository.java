package com.eshop.admin.brand;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.eshop.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
	public Long countById(Integer id);
}
