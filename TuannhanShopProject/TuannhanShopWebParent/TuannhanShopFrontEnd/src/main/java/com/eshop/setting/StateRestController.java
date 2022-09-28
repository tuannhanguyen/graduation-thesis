package com.eshop.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.State;
import com.eshop.common.entity.StateDTO;

@RestController
public class StateRestController {
	
	@Autowired private StateRepository stateRepository;
	
	@GetMapping("/setting/list_states_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
		List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> result = new ArrayList<>();
		
		for (State state : listStates) {
			result.add(new StateDTO(state.getId(), state.getName()));
		}
		
		return result;
	}
}
