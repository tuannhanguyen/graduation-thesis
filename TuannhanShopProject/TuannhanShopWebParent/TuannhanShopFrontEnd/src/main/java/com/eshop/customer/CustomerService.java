package com.eshop.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
public class CustomerService {

	@Autowired private CountryRepository countryRepository;
	@Autowired private CustomerRepository customerRepository;
	@Autowired PasswordEncoder passwordEncoder;

	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(String email) {
		Customer customer = customerRepository.findByEmail(email);
		return customer == null;
	}

	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());

		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);

		customerRepository.save(customer);
	}

	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);

	}

	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	public Customer getCustomerByVerificationCode(String verificationCode) {
	    return customerRepository.findByVerificationCode(verificationCode);
	}

	public void updateCustomer(Customer customerInForm) {
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		if (customerInForm.getPassword().isEmpty()) {
			customerInForm.setPassword(customerInDB.getPassword());
		} else {
			encodePassword(customerInForm);
		}
		customerRepository.save(customerInForm);
	}

}
