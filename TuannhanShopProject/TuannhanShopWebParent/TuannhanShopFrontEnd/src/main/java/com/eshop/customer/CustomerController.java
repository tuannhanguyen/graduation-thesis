package com.eshop.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;

@Controller
public class CustomerController {

	@Autowired private CustomerService customerService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listAllCountries = customerService.listAllCountries();
		
		model.addAttribute("listAllCountries", listAllCountries);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());
		
		return "register/register_form";
	}
}
