package com.eshop.admin.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.eshop.common.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	@Query("SELECT u from User u WHERE u.email = ?1")
	public User getUserByEmail(String email);

}
