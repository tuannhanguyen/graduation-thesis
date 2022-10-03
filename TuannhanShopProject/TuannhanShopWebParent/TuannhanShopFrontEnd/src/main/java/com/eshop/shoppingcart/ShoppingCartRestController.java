package com.eshop.shoppingcart;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshop.Utility;
import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerNotFoundException;
import com.eshop.customer.CustomerService;

@RestController
public class ShoppingCartRestController {

	@Autowired private ShoppingCartService shoppingCartService;
	@Autowired private CustomerService customerService;

	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity,
			HttpServletRequest request) {

		try {
			Customer customer = getAuthenticatedCustomer(request);
			Integer updatedQuantity = shoppingCartService.addProduct(productId, quantity, customer);
			return updatedQuantity + " item(s) of this product were added to your shopping cart";
		} catch (CustomerNotFoundException e) {
			return "You must login to add this product to cart";
		} catch (ShoppingCartException e) {
			return e.getMessage();
		}
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOrAuthenticatedCustomer(request);

		if (email == null) {
			throw new CustomerNotFoundException("No Authenticated customer");
		}

		return customerService.getCustomerByEmail(email);

	}

	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity,
			HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subTotal = shoppingCartService.updateQuantity(productId, quantity, customer);
			return String.valueOf(subTotal);
		} catch (CustomerNotFoundException e) {
			return "You must login to change quantity of product";
		}
	}

	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable("productId") Integer id, HttpServletRequest request) {
		try {
			Customer customer  = getAuthenticatedCustomer(request);
			shoppingCartService.removeProduct(id, customer);

			return "The product has been romove from your shopping cart";
		} catch (CustomerNotFoundException e) {
			return "You must logn to remove product";
		}
	}
}