package ru.practicum.shareit.request.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryTest {

	@Autowired
	private RequestRepository requestRepository;

	@Autowired
	private UserRepository userRepository;

	private ItemRequest itemRequestFirst;
	private ItemRequest itemRequestSecond;
	private User userOwner;
	private User userLeaser;
	private final Sort byCreated = Sort.by(Sort.Direction.DESC, "Created");


	@BeforeEach
	void setUp_createUsersAndItemRequests() {
		userOwner = User.builder()
				.name("username")
				.email("usermail@gmail.com")
				.build();

		userLeaser = User.builder()
				.name("usernameLease")
				.email("usermailLease@gmail.com")
				.build();

		itemRequestFirst = ItemRequest.builder()
				.description("description")
				.created(LocalDateTime.of(2024, 3, 15, 15, 30, 0))
				.items(new ArrayList<>())
				.user(userOwner)
				.build();

		itemRequestSecond = ItemRequest.builder()
				.description("description2")
				.created(LocalDateTime.of(2024, 3, 16, 15, 30, 0))
				.items(new ArrayList<>())
				.user(userOwner)
				.build();
	}

	@Test
	void findById_whenSearchForItemRequestFirstById_thenReturnItemRequestFirst() {
		assertNull(itemRequestFirst.getId());
		assertNull(userOwner.getId());
		userRepository.save(userOwner);
		requestRepository.save(itemRequestFirst);
		Optional<ItemRequest> optionalItemRequest = requestRepository.findById(itemRequestFirst.getId());
		assertTrue(optionalItemRequest.isPresent());
		assertEquals(optionalItemRequest.get().getId(), itemRequestFirst.getId());
		assertEquals(optionalItemRequest.get().getDescription(), "description");
	}

	@Test
	void findByUserId_whenSearchForItemRequestsByUserId_thenReturnListOfIRsWithTwoIR() {
		userRepository.save(userOwner);
		requestRepository.save(itemRequestFirst);
		requestRepository.save(itemRequestSecond);
		List<ItemRequest> list = requestRepository.findByUserId(userOwner.getId(), byCreated);
		assertEquals(list.size(), 2);
		assertEquals(list.get(0).getId(), itemRequestSecond.getId());
		assertEquals(list.get(1).getId(), itemRequestFirst.getId());
	}

	@Test
	void findByUserIdNot_whenSearchForItemRequestsOfOtherUsers_thenReturnListOfIRsWithOneIRWithoutIROfItsOwner() {
		userRepository.save(userOwner);
		userRepository.save(userLeaser);
		itemRequestSecond.setUser(userLeaser);
		requestRepository.save(itemRequestFirst);
		requestRepository.save(itemRequestSecond);
		PageRequest page = PageRequest.of(0, 1).withSort(byCreated);

		List<ItemRequest> list = requestRepository.findByUserIdNot(userOwner.getId(), page).getContent();
		assertEquals(list.size(), 1L);
		assertEquals(list.get(0).getId(), itemRequestSecond.getId());
		itemRequestSecond.setUser(userOwner);
	}

	@AfterEach
	void cleanUp() {
		requestRepository.deleteAll();
		userRepository.deleteAll();
	}
}
