package ru.practicum.shareit.item.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dto.CommentShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CommentRepository commentRepository;

	private User testUser;
	private Item item;
	private Comment comment;

	@BeforeEach
	void setUp_createUserAndItem() {
		testUser = userRepository.save(User.builder()
				.email("email@mail.ru")
				.name("name")
				.build());

		item = itemRepository.save(Item.builder()
				.id(1L)
				.name("item")
				.description("description")
				.owner(testUser)
				.available(true)
				.build());

		comment = commentRepository.save(Comment.builder()
				.text("comment")
				.item(item)
				.author(testUser)
				.created(LocalDateTime.now())
				.build());
	}

	@Test
	void findAllByItemId() {
		List<CommentShort> comments = commentRepository.findAllByItemId(item.getId());
		assertEquals(1, comments.size());
	}

	@AfterEach
	void cleanUp() {
		itemRepository.deleteAll();
		commentRepository.deleteAll();
	}
}