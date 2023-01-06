package com.eshop.customer;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.Utility;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.security.CustomerUserDetails;
import com.eshop.setting.CountryRepository;
import com.eshop.setting.EmailDetails;
import com.eshop.setting.EmailService;
import com.eshop.shoppingcart.ShoppingCartService;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	ShoppingCartService cartService;

	@Autowired private EmailService emailService;

	@Autowired HttpSession httpSession;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listAllCountries = customerService.listAllCountries();

		model.addAttribute("listAllCountries", listAllCountries);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());

		return "register/register_form";
	}

	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model) {
	    EmailDetails emailDetails = new EmailDetails();
	    emailDetails.setSubject("Register new account");
	    emailDetails.setRecipient(customer.getEmail());

	    int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
        String content = "http://localhost/Shopme/register/verify?verification_code=" + generatedString;
        emailDetails.setMsgBody(content);
        emailService.sendSimpleMail(emailDetails);

        httpSession.setAttribute("verification_code_register", generatedString);
        httpSession.setAttribute("customer_session", customer);

		model.addAttribute("message", "Check your email to verify");
		model.addAttribute("pageTitle", "Register");
		return "customer/message";
	}

	@GetMapping("/register/verify")
	public String registerSuccess(HttpServletRequest request, Model model) {
	    String verification_code = request.getParameter("verification_code");
	    String verification_code_session = (String) httpSession.getAttribute("verification_code_register");

	    if (!verification_code.equals(verification_code_session)) {
	        model.addAttribute("message", "Verification code invalid");
	        model.addAttribute("pageTitle", "Verification code invalid");
	        return "customer/message";
	    }

        Customer customer = (Customer) httpSession.getAttribute("customer_session");
        customerService.registerCustomer(customer);
        httpSession.removeAttribute("customer_session");

        model.addAttribute("pageTitle", "Register success");

	    return "/register/register_success";
	}

	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		String email = getEmailOfAuthenticatiedCustomer(request);
		Customer customer = customerService.getCustomerByEmail(email);

		List<Country> listAllCountries = countryRepository.findAllByOrderByNameAsc();

		model.addAttribute("customer", customer);
		model.addAttribute("listAllCountries", listAllCountries);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                Integer quantityInCart = cartService.countQuantityinCart(customer);
                model.addAttribute("quantityInCart", quantityInCart);

        }

		return "customer/account_form";
	}

	@PostMapping("/update_account_details")
	public String updateAccountDetails(Model model, Customer customer, RedirectAttributes ra,
			HttpServletRequest request) {
		customerService.updateCustomer(customer);
		ra.addFlashAttribute("message", "Your account details have been updated");

		updateNameForAuthenticatedCustomer(customer, request);

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/account_details";

		if ("address_book".equals(redirectOption)) {
			redirectURL = "redirect:/address_book";
		} else if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/address_book?redirect=checkout";
		}

		return redirectURL;

	}

	private String getEmailOfAuthenticatiedCustomer(HttpServletRequest request) {

		Object principal = request.getUserPrincipal();
		String customerEmail = null;
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		}

		return customerEmail;
	}

	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		String fullName = customer.getFirstName() + " " + customer.getLastName();
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			fullName = request.getUserPrincipal().getName();
			CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
			Customer authenticatedCustomer = userDetails.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName());
		}
	}

	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails userDetails = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		} else if (principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}

		return userDetails;
	}

	@GetMapping("/forgot_password")
	public String forgotPasswordForm() {
	    return "customer/forgot_password_form";
	}

	@PostMapping("/forgot_password")
    public String resetPasswordForm(@RequestParam String email, HttpServletRequest request, Model model) throws CustomerNotFoundException {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    EmailDetails emailDetails = new EmailDetails();
	    emailDetails.setRecipient(email);
	    emailDetails.setSubject("Reset Password");
	    emailDetails.setMsgBody("http://localhost/Shopme/reset_password?verification_code=" + generatedString);

	    Customer customer = customerService.getCustomerByEmail(email);
	    if (customer != null) {
	        customer.setVerificationCode(generatedString);
	        customerService.updateCustomer(customer);
	    }

	    emailService.sendSimpleMail(emailDetails);
	    model.addAttribute("message", "Please check your email");

	    model.addAttribute("pageTitle", "Forgot password");

        return "customer/message";
    }

	@GetMapping("/reset_password")
	public String resetPasswordForm(@RequestParam String verification_code, Model model) {
	    Customer customerByVerificationCode = customerService.getCustomerByVerificationCode(verification_code);
	    if (customerByVerificationCode == null) {
	        model.addAttribute("message", "Verification code invalid");
	        return "customer/message.html";
	    }
	    httpSession.setAttribute("verification_code", verification_code);
	    return "customer/reset_password_form";
	}

	@PostMapping("/reset_password")
    public String resetPassword(@RequestParam String password, Model model) {
	    String verification_code = (String) httpSession.getAttribute("verification_code");
        Customer customerByVerificationCode = customerService.getCustomerByVerificationCode(verification_code);
        customerByVerificationCode.setPassword(password);
        customerByVerificationCode.setVerificationCode(null);
        customerService.updateCustomer(customerByVerificationCode);
//        if (customerByVerificationCode == null) {
//            model.addAttribute("message", "Success");
//            return "customer/message.html";
//        }
        httpSession.removeAttribute("verification_code");
        model.addAttribute("message", "You have successfully changed your password");
        model.addAttribute("pageTitle", "Forgot password");
        return "customer/message";
    }

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No Authenticated customer");
        }

        return customerService.getCustomerByEmail(email);
    }
}
