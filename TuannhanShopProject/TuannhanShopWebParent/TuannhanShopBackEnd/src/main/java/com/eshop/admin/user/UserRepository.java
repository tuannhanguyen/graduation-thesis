package com.eshop.admin.user;

import org.springframework.data.repository.CrudRepository;

import com.eshop.common.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
