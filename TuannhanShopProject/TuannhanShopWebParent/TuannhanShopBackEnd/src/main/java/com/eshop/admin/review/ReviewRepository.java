package com.eshop.admin.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.eshop.admin.paging.SearchRepository;
import com.eshop.common.entity.Review;

public interface ReviewRepository extends SearchRepository<Review, Integer> {

    @Override
    @Query("SELECT r FROM Review r WHERE r.headline LIKE %?1% OR "
            + "r.comment LIKE %?1% OR r.product.name LIKE %?1% OR "
            + "CONCAT(r.customer.firstName, ' ', r.customer.lastName) LIKE %?1%")
    public Page<Review> findAll(String keyword, Pageable pageable);

    @Override
    public List<Review> findAll();
}