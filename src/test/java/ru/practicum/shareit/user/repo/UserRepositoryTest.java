package ru.practicum.shareit.user.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = userRepository.save(User.builder()
				.email("email@mail.ru")
				.name("name")
				.build());
	}

	@Test
	void save() {
		User newUser = User.builder().email("email2@mail.ru").name("name2").build();
		User user = userRepository.save(newUser);
		assertEquals("name2", user.getName());
	}

	@Test
	void findAll() {
		List<User> users = userRepository.findAll();
		assertEquals("name", users.get(0).getName());
	}

	@Test
	void findById() {
		Optional<User> user = userRepository.findById(testUser.getId());
		assertTrue(user.isPresent());
		assertEquals(user.get().getEmail(), "email@mail.ru");
	}

	@AfterEach
	void cleanUp() {
		userRepository.deleteAll();
	}
}