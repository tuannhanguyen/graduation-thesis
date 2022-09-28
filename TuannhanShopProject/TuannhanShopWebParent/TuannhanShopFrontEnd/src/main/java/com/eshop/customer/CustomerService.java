package com.eshop.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.setting.CountryRepository;

@Service
public class CustomerService {
	
	@Autowired private CountryRepository countryRepository;
	@Autowired private CustomerRepository customerRepository;
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepository.findByEmail(email);
		return customer == null;
	}

}
