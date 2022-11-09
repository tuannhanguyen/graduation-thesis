package com.eshop.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.eshop.common.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	@Query("SELECT u from User u WHERE u.email = ?1")
	public User getUserByEmail(String email);

	public Long countById(Integer id);

	@Query("SELECT u from User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);

	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);

	@Query("SELECT COUNT ( u.enabled )FROM User u WHERE u.enabled = true")
	public int countUserEnabled();

	@Query("SELECT COUNT ( u.enabled )FROM User u WHERE u.enabled = false")
    public int countUserDisabled();

}
