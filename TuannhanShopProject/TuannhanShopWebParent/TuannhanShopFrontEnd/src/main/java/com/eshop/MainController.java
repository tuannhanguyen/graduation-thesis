package com.eshop;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.category.CategoryService;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Customer;
import com.eshop.shoppingcart.ShoppingCartService;

@Controller
public class MainController {

	@Autowired CategoryService categoryService;
	@Autowired ControllerHelper controllerHelper;
	@Autowired ShoppingCartService cartService;

	@GetMapping("/")
	public String viewHomePage(Model model, HttpServletRequest request) {
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
                Integer quantityInCart = cartService.countQuantityinCart(customer);
                model.addAttribute("quantityInCart", quantityInCart);

        }

		return "index";
	}

	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}

		return "redirect:/";
	}
}
