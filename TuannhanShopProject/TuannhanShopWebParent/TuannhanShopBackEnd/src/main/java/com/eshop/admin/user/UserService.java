package com.eshop.admin.user;

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

import com.eshop.common.entity.Role;
import com.eshop.common.entity.User;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 5;

	@Autowired UserRepository userRepository;

	@Autowired RoleRepository roleRepository;

	@Autowired PasswordEncoder passwordEncoder;

	public User getByEmail(String email) {
		return userRepository.getUserByEmail(email);
	}

	public List<User> listAll() {
		return (List<User>) userRepository.findAll();
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
 		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

 		if (keyword != null) {
 			return userRepository.findAll(keyword, pageable);
 		}

		return userRepository.findAll(pageable);
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);

		if (isUpdatingUser) {
			User existingUser = userRepository.findById(user.getId()).get();

			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodedPassword(user);
			}
		} else {
			encodedPassword(user);
		}

		return userRepository.save(user);
	}

	public User updateAccount(User userInForm) {
		User userInDb = userRepository.findById(userInForm.getId()).get();

		if(!userInDb.getPassword().isEmpty()) {
			userInDb.setPassword(userInForm.getPassword());
			encodedPassword(userInDb);
		}

		if (userInForm.getPhotos() != null) {
			userInDb.setPhotos(userInForm.getPhotos());
		}

		userInDb.setFirstName(userInForm.getFirstName());
		userInDb.setLastName(userInForm.getLastName());

		return userRepository.save(userInDb);
	}

	private void encodedPassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepository.getUserByEmail(email);

		if (userByEmail == null) return true;

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			if (userByEmail != null) return false;
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}

		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}

	public void delete(Integer id) throws UserNotFoundException {
		Long countByid = userRepository.countById(id);
		if (countByid == null || countByid == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}

		userRepository.deleteById(id);
	}

	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepository.updateEnabledStatus(id, enabled);
	}

	public int countUserEnabled() {
	    return userRepository.countUserEnabled();
	}

	public int countUserDisabled() {
        return userRepository.countUserDisabled();
    }
}
