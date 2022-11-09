package com.eshop.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.common.entity.Product;

@Service
@Transactional
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 5;

	@Autowired ProductRepository productRepository;

	public List<Product> listAll() {
		return (List<Product>) productRepository.findAll();
	}

	public Page<Product> listByPage(int pageNum, String sortField, String sortDir,
			String keyword, Integer categoryId) {
		Sort sort = Sort.by(sortField);

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			}
			return productRepository.findAll(keyword, pageable);
		}

		if (categoryId != null && categoryId > 0) {
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return productRepository.findAllInCategories(categoryId, categoryIdMatch, pageable);
		}

		return productRepository.findAll(pageable);
	}

	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}

		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replace(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replace(" ", "-"));
		}

		product.setUpdatedTime(new Date());

		return productRepository.save(product);
	}

	public void saveProductPrice(Product productInForm) {
		Product productInDb = productRepository.findById(productInForm.getId()).get();
		productInDb.setCost(productInForm.getCost());
		productInDb.setPrice(productInForm.getPrice());
		productInDb.setDiscountPercent(productInForm.getDiscountPercent());

		productRepository.save(productInDb);

	}

	public String checkUnique(Integer id, String  name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = productRepository.findByName(name);
		if (isCreatingNew) {
			if (productByName != null) {
				return "Duplicated";
			}
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicated";
			}
		}

		return "OK";
	}

	public void updateEnabledStatus(Integer id, boolean status) {
		productRepository.updateEnabledStatus(id, status);
	}

	public void delete(Integer id) throws ProductNotFoundException {
		Long countById = productRepository.countById(id);

		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}

		productRepository.deleteById(id);
	}

	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return productRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}

	public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();
		Page<Product> page = productRepository.searchProductsByName(keyword, pageable);
		helper.updateModelAttributes(pageNum, page);
	}

	public int countProductEnabled() {
	    return productRepository.countProductEnabled();
	}

	public int countProductDisabled() {
        return productRepository.countProductDisabled();
    }

	public int countProductInStock() {
	    return productRepository.countProductInStock();
	}

	public int countProductOutStock() {
	    return productRepository.countProductOutStock();
	}
}
