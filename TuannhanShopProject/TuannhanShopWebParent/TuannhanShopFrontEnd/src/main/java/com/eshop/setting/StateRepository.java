package com.eshop.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer>{

	public List<State> findByCountryOrderByNameAsc(Country country);

}
