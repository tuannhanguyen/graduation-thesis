package com.eshop.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.ControllerHelper;
import com.eshop.Utility;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerNotFoundException;
import com.eshop.customer.CustomerService;
import com.eshop.shoppingcart.ShoppingCartService;

@Controller
public class AddressController {

	@Autowired private AddressService addressService;
	@Autowired private CustomerService customerService;
	@Autowired ControllerHelper controllerHelper;
    @Autowired ShoppingCartService cartService;

	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) throws CustomerNotFoundException {
		Customer customer = getAuthenticatedCustomer(request);
		List<Address> listAddresses = addressService.listAddressBook(customer);

		boolean usePrimaryAddressAsDefault = true;
		for (Address address : listAddresses) {
			if (address.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}

		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("customer", customer);
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Integer quantityInCart = cartService.countQuantityinCart(customer);
            model.addAttribute("quantityInCart", quantityInCart);

        }

		return "address_book/addresses";
	}

	@GetMapping("/address_book/new")
	public String newAddress(Model model, HttpServletRequest request) {
		List<Country> listCountries = customerService.listAllCountries();

		model.addAttribute("listCountries", listCountries);
		model.addAttribute("address", new Address());
		model.addAttribute("pageTitle", "Add New Address");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
                Integer quantityInCart = cartService.countQuantityinCart(customer);
                model.addAttribute("quantityInCart", quantityInCart);

        }

		return "address_book/address_form";
	}

	@PostMapping("/address_book/save")
	public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) throws CustomerNotFoundException {
		Customer customer = getAuthenticatedCustomer(request);

		address.setCustomer(customer);
		addressService.save(address);

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";

		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}

		ra.addFlashAttribute("message", "The address has been saved successfully.");

		return redirectURL;
	}

	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id") Integer addressId, Model model,
			HttpServletRequest request) throws CustomerNotFoundException {
		Customer customer = getAuthenticatedCustomer(request);
		List<Country> listCountries = customerService.listAllCountries();

		Address address = addressService.get(addressId, customer.getId());

		model.addAttribute("address", address);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");

		return "address_book/address_form";
	}

	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
			HttpServletRequest request) throws CustomerNotFoundException {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.delete(addressId, customer.getId());

		ra.addFlashAttribute("message", "The address ID " + addressId + " has been deleted.");

		return "redirect:/address_book";
	}

	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId,
			HttpServletRequest request) throws CustomerNotFoundException {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";

		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/checkout";
		}

		return redirectURL;
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("No Authenticated customer");
		}

		return customerService.getCustomerByEmail(email);
	}
}