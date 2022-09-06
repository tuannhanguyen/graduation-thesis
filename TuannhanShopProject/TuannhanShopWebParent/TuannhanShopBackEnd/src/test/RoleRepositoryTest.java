package com.eshop.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import 

import com.eshop.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RoleRepositoryTest {
	
	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "manage everything")
		Role savedRole = repo.save(roleAdmin);
		
		assertThat(savedRole.getId().isGreaterThan(0));
	}

}
