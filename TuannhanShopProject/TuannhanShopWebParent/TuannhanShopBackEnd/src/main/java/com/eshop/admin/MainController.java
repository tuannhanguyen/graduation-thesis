package com.eshop.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.admin.category.CategoryService;
import com.eshop.admin.customer.CustomerService;
import com.eshop.admin.order.OrderService;
import com.eshop.admin.product.ProductService;
import com.eshop.admin.review.ReviewService;
import com.eshop.admin.shippingrate.ShippingRateService;
import com.eshop.admin.user.UserService;
import com.eshop.common.entity.OrderStatus;

@Controller
public class MainController {

    @Autowired UserService userService;
    @Autowired ProductService productService;
    @Autowired ShippingRateService shippingRateService;
    @Autowired ReviewService reviewService;
    @Autowired CustomerService customerService;
    @Autowired OrderService orderService;
    @Autowired CategoryService categoryService;

	@GetMapping("/")
	public String viewHomePage(Model model) {
	    int countUserEnabled = userService.countUserEnabled();
	    int countUserDisabled = userService.countUserDisabled();
	    model.addAttribute("countUserEnabled", countUserEnabled);
        model.addAttribute("countUserDisabled", countUserDisabled);

        int countProductEnabled = productService.countProductEnabled();
        int countProductDisabled = productService.countProductDisabled();
        int countProductInStock = productService.countProductInStock();
        int countProductOutStock = productService.countProductOutStock();
        model.addAttribute("countProductEnabled", countProductEnabled);
        model.addAttribute("countProductDisabled", countProductDisabled);
        model.addAttribute("countProductInStock", countProductInStock);
        model.addAttribute("countProductOutStock", countProductOutStock);

        int countCodSupported = shippingRateService.countCodSupported();
        model.addAttribute("countCodSupported", countCodSupported);

        int countProductReviewed = reviewService.countProductReviewed();
        model.addAttribute("countProductReviewed", countProductReviewed);

        int countCustomerEnabled = customerService.countCustomerEnabled();
        int countCustomerDisabled = customerService.countCustomerDisabled();
        model.addAttribute("countCustomerEnabled", countCustomerEnabled);
        model.addAttribute("countCustomerDisabled", countCustomerDisabled);

        int countOrderNew = orderService.countStatus(OrderStatus.NEW);
        int countOrderDelivered = orderService.countStatus(OrderStatus.DELIVERED);
        int countOrderShipping = orderService.countStatus(OrderStatus.SHIPPING);
        int countOrderProcessing= orderService.countStatus(OrderStatus.PROCESSING);
        int countOrderCanceled= orderService.countStatus(OrderStatus.CANCELLED);
        model.addAttribute("countOrderNew", countOrderNew);
        model.addAttribute("countOrderDelivered", countOrderDelivered);
        model.addAttribute("countOrderShipping", countOrderShipping);
        model.addAttribute("countOrderProcessing", countOrderProcessing);
        model.addAttribute("countOrderCanceled", countOrderCanceled);
        
        int countRootCategory = categoryService.countRootCategory();
        int countCategoryEnabled = categoryService.countCategoryEnabled();
        int countCategoryDisabled = categoryService.countCategoryDisabled();
        model.addAttribute("countRootCategory", countRootCategory);
        model.addAttribute("countCategoryEnabled", countCategoryEnabled);
        model.addAttribute("countCategoryDisabled", countCategoryDisabled);

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
