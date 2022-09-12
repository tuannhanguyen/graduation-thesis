package com.eshop.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.admin.security.ShopUserDetails;
import com.eshop.common.entity.User;

@Controller
public class AccountController {

	@Autowired
	private UserService userService;

	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopUserDetails loggedUser,
			Model model) {
		String email = loggedUser.getUsername();
		User user = userService.getByEmail(email);
		model.addAttribute("user", user);

		return "account_form";
	}
}
