package ru.practicum.shareit.item.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private UserRepository userRepository;

	private Item testItemFirst;
	private Item testItemSecond;
	private User testUser;

	@BeforeEach
	void setUp_createUserAndItem() {
		testUser = userRepository.save(User.builder()
				.email("email@mail.ru")
				.name("name")
				.build());

		testItemFirst = itemRepository.save(Item.builder()
				.name("item")
				.description("description")
				.owner(testUser)
				.available(true)
				.build());

		testItemSecond = itemRepository.save(Item.builder()
				.name("name")
				.description("item")
				.owner(testUser)
				.available(true)
				.build());
	}

	@Test
	void findByUserId() {
		List<Item> items = itemRepository.findByOwnerId(testUser.getId());
		assertEquals("item", items.get(0).getName());
		assertEquals("description", items.get(0).getDescription());
		assertTrue(items.get(0).getAvailable());
		assertEquals(testUser, items.get(0).getOwner());

	}

	@Test
	void findByNameOrDescriptionContainingAndAvailableTrue_twoItems() {
		Pageable page = Pageable.unpaged();
		Page<Item> items = itemRepository
				.findByNameOrDescriptionContainingAndAvailableTrue("item", page);
		assertEquals(2, items.getSize());
	}

	@Test
	void findByNameOrDescriptionContainingAndAvailableTrue_oneItem() {
		Pageable page = Pageable.unpaged();
		Page<Item> items = itemRepository
				.findByNameOrDescriptionContainingAndAvailableTrue("description", page);
		assertEquals(1, items.getSize());
	}

	@AfterEach
	void cleanUp() {
		itemRepository.deleteAll();
		userRepository.deleteAll();
	}
}