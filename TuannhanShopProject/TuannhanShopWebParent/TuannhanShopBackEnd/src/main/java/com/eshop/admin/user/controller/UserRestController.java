package com.eshop.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshop.admin.user.UserService;

@RestController
public class UserRestController {
	@Autowired UserService userService;
	
	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(Integer id, String email) {
		return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
	}
}
