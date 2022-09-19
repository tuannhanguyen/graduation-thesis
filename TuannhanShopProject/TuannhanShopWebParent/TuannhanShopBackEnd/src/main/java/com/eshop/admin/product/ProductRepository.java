package com.eshop.admin.product;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.eshop.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	public Long countById(Integer id);

	public Product findByName(String name);

	@Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);

}
