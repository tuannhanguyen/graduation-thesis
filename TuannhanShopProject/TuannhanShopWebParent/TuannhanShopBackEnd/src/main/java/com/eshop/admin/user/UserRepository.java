package com.eshop.admin.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.eshop.common.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	@Query("SELECT u from User u WHERE u.email = ?1")
	public User getUserByEmail(String email);

	public Long countById(Integer id);

	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);

}
