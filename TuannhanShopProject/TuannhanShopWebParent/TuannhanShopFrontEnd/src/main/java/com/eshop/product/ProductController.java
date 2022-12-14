package com.eshop.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.eshop.ControllerHelper;
import com.eshop.category.CategoryService;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Product;
import com.eshop.common.entity.Review;
import com.eshop.common.exception.ProductNotFoundException;
import com.eshop.review.ReviewService;
import com.eshop.review.vote.ReviewVoteService;
import com.eshop.shoppingcart.ShoppingCartService;

@Controller
public class ProductController {

	@Autowired ProductService productService;
	@Autowired private CategoryService categoryService;
	@Autowired private ReviewService reviewService;
    @Autowired private ReviewVoteService voteService;
    @Autowired private ControllerHelper controllerHelper;
    @Autowired ShoppingCartService cartService;
    @Autowired
    private HttpSession httpSession;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
			Model model, HttpServletRequest request) {
		return viewCategoryByPage(alias, 1, model, request);
	}

	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable(name = "pageNum") int pageNum,
			Model model,  HttpServletRequest request) {
		Category category = categoryService.getCategory(alias);
		if (category == null) {
			return "error/404";
		}

		List<Category> listCategoryParents = categoryService.getCategoryParents(category);

		Page<Product> pageProducts = productService.listByCategories(pageNum, category.getId());
		List<Product> listProducts = pageProducts.getContent();
		httpSession.setAttribute("listProducts", listProducts);

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}

		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("pageTitle", category.getName());
		model.addAttribute("listCategoryParents", listCategoryParents);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("category", category);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            Integer quantityInCart = cartService.countQuantityinCart(customer);
            model.addAttribute("quantityInCart", quantityInCart);

        }


		return "product/products_by_category";
	}

	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,
			HttpServletRequest request) {
		try {
			Product product = productService.getProduct(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			Page<Review> listReviews = reviewService.list3MostVotedReviewsByProduct(product);

			Customer customer = controllerHelper.getAuthenticatedCustomer(request);

			if (customer != null) {
				boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
				voteService.markReviewsVotedForProductByCustomer(listReviews.getContent(), product.getId(), customer.getId());

				if (customerReviewed) {
					model.addAttribute("customerReviewed", customerReviewed);
				} else {
					boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
					model.addAttribute("customerCanReview", customerCanReview);
				}
			}

			List<Product> listProducts = (List<Product>) httpSession.getAttribute("listProducts");

			model.addAttribute("product", product);
			model.addAttribute("listReviews", listReviews);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("pageTitle", product.getName());
			model.addAttribute("listProducts", listProducts);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
	            Integer quantityInCart = cartService.countQuantityinCart(customer);
	            model.addAttribute("quantityInCart", quantityInCart);

	        }

			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}

	@GetMapping("/search")
	public String searchFirstPage(@Param("keyword") String keyword, Model model, HttpServletRequest request) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            Integer quantityInCart = cartService.countQuantityinCart(customer);
            model.addAttribute("quantityInCart", quantityInCart);

        }
		return searchByPage(keyword, 1, model, request);
	}

	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@Param("keyword") String keyword,
			@PathVariable("pageNum") int pageNum,
			Model model, HttpServletRequest request) {
		Page<Product> pageProducts = productService.search(keyword, pageNum);
		List<Product> listResult = pageProducts.getContent();

		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}

		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("pageTitle", keyword + "- Search Result");

		model.addAttribute("keyword", keyword);
		model.addAttribute("searchKeyword", keyword);
		model.addAttribute("listResult", listResult);

		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
	            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
	            Integer quantityInCart = cartService.countQuantityinCart(customer);
	            model.addAttribute("quantityInCart", quantityInCart);

	        }

		return "product/search_result";
	}
}


