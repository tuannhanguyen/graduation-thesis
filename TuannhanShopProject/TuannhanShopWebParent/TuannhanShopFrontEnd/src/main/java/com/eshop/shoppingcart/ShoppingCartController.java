package com.eshop.shoppingcart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.Utility;
import com.eshop.address.AddressService;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import com.eshop.customer.CustomerNotFoundException;
import com.eshop.customer.CustomerService;
import com.eshop.shipping.ShippingRateService;

@Controller
public class ShoppingCartController {

	@Autowired ShoppingCartService shoppingCartService;
	@Autowired private CustomerService customerService;
	@Autowired private AddressService addressService;
	@Autowired private ShippingRateService shipService;
	
	@GetMapping("/cart")
	public String viewShoppingCart(Model model, HttpServletRequest request) throws CustomerNotFoundException {
		
		//authenticated Customer
		Customer customer = getAuthenticatedCustomer(request);
		List<CartItem> listCartItems = shoppingCartService.listCartItems(customer);
		
		model.addAttribute("listCartItems", listCartItems);
		
		float estimatedTotal = 0.0F;
		for(CartItem item : listCartItems) {
			estimatedTotal += item.getSubTotal();
		}
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressAsDefault = false;
		
		if (defaultAddress != null) {
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
		} else {
			usePrimaryAddressAsDefault = true;
			shippingRate = shipService.getShippingRateForCustomer(customer);
		}
		
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		model.addAttribute("shippingSupported", shippingRate != null);
		model.addAttribute("listCartItems", listCartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		
		return "cart/shopping_cart";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("No Authenticated customer");
		}
		
		return customerService.getCustomerByEmail(email);
	}
	
	
}