package com.eshop.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Brand;

@Service
public class BrandService {
	public static final int BRANDS_PER_PAGE = 10;
	@Autowired BrandRepository brandRepository;

	public List<Brand> listAll() {
		return (List<Brand>) brandRepository.findAll();
	}
	
	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
		
		if (keyword != null) {
			return brandRepository.findAll(keyword, pageable);
		}
		
		return brandRepository.findAll(pageable);
	}

	public Brand save(Brand brand) {
		return brandRepository.save(brand);
	}

	public Brand get(Integer id) throws BrandNotFoudException {
		try {
			return brandRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new BrandNotFoudException("Could not find any brand with ID " + id);
		}
	}

	public void delete(Integer id) throws BrandNotFoudException {
		Long countById = brandRepository.countById(id);

		if (countById == null || countById == 0) {
			throw new BrandNotFoudException("Could not find any brand with ID " + id);
		}

		brandRepository.deleteById(id);
	}
	
	public String checkUniqueName(Integer id, String name) {
		
		boolean isCreatingNew = (id == null);
		
		Brand brand = brandRepository.findByName(name);
		
		if (isCreatingNew) {
			if (brand != null) {
				return "DuplicatedName";
			}
		} else {
			if (brand.getId() != id) 
				return "DuplicatedName";
		}
		
		return "OK";
	}

}
