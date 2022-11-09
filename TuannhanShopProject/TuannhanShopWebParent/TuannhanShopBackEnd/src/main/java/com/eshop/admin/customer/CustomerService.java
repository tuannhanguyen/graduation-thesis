package com.eshop.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eshop.admin.setting.country.CountryRepository;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;

@Service
@Transactional
public class CustomerService {
	public static final int CUSTOMERS_PER_PAGE = 5;

	@Autowired CustomerRepository customerRepository;
	@Autowired CountryRepository countryRepository;
	@Autowired PasswordEncoder passwordEncoder;

	public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
 		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMERS_PER_PAGE, sort);

 		if (keyword != null) {
 			return customerRepository.findAll(keyword, pageable);
 		}

		return customerRepository.findAll(pageable);
	}


	public void save(Customer customerInForm) {
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}

		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());

		customerRepository.save(customerInForm);
	}

	public boolean isEmailUnique(Integer id, String email) {
		Customer existingCustomer = customerRepository.getCustomerByEmail(email);

		if (existingCustomer != null && existingCustomer.getId() != id) return false;

		return true;
	}

	public Customer get(Integer id) throws CustomerNotFoundException{
		try {
			return customerRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not find any customer with ID " + id);
		}
	}

	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public void delete(Integer id) throws CustomerNotFoundException {
		Long countByid = customerRepository.countById(id);
		if (countByid == null || countByid == 0) {
			throw new CustomerNotFoundException("Could not find any customer with ID " + id);
		}

		customerRepository.deleteById(id);
	}

	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepository.updateEnabledStatus(id, enabled);
	}

	public int countCustomerEnabled() {
	    return customerRepository.countCustomerEnabled();
	}

	public int countCustomerDisabled() {
        return customerRepository.countCustomerDisabled();
    }
}
