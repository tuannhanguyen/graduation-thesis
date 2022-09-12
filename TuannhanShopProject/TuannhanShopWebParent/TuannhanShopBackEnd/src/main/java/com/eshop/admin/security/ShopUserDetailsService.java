package com.eshop.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.eshop.admin.user.UserRepository;
import com.eshop.common.entity.User;

public class ShopUserDetailsService implements UserDetailsService {

	@Autowired UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByEmail(username);

		if (user != null) {
			return new ShopUserDetails(user);
		}

		throw new UsernameNotFoundException("Could not find user with email: " + username);
	}

}
