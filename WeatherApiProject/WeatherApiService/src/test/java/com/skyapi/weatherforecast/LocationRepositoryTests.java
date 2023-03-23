package com.skyapi.weatherforecast;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTests {

	@Autowired
	private LocationRepository repo;
	
	@Test
	public void testAddSuccess() {
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		
		Location savedLocation = repo.save(location);
		
		assertThat(savedLocation).isNotNull();
		assertThat(savedLocation.getCode()).isEqualTo("NYC_USA");
		
	}
}
