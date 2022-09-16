package com.eshop.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Brand;

@Service
public class BrandService {
	@Autowired BrandRepository brandRepository;

	public List<Brand> listAll() {
		return (List<Brand>) brandRepository.findAll();
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

}
