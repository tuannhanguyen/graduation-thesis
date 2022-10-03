package com.eshop;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
public class Utility {

	public static String getSiteUrl(HttpServletRequest request) {

		String siteURL = request.getRequestURL().toString(); // return full URL of current request
		return siteURL.replace(request.getServletPath(), "");
	}


	public static String getEmailOrAuthenticatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		if(principal == null) return null;

		String customerEmail = null;

		// chi login = db or remember me moi lay duoc email;
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		}
		return customerEmail;
	}
}