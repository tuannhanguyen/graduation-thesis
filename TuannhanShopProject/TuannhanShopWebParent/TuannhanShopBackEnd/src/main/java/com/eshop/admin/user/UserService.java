package com.eshop.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.User;

@Service
public class UserService {

	@Autowired UserRepository userRepository;

	public List<User> listAll() {
		return (List<User>) userRepository.findAll();
	}
}
