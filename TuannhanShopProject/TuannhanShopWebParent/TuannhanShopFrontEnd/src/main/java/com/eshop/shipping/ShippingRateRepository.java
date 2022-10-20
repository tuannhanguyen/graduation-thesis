package com.eshop.shipping;

import org.springframework.data.repository.CrudRepository;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
	public ShippingRate findByCountryAndState(Country country, String state);
}