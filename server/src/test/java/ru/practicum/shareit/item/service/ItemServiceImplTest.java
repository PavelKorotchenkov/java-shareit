package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private UserServiceImpl userService;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private ItemServiceImpl itemService;

	@Captor
	private ArgumentCaptor<Item> argumentCaptor;

	@Test
	void add_whenNewItemAdded_thenReturnCorrectItem() {
		ItemCreateDto itemToSave = ItemCreateDto.builder()
				.name("name")
				.description("description")
				.available(true)
				.build();
		Item itemEntity = ItemDtoMapper.ofItemCreateDto(itemToSave);
		when(userService.getById(anyLong())).thenReturn(UserDto.builder().build());
		when(itemRepository.save(itemEntity)).thenReturn(itemEntity);

		ItemDto actualItem = itemService.add(itemToSave);

		assertEquals(itemToSave.getName(), actualItem.getName());
		assertEquals(itemToSave.getDescription(), actualItem.getDescription());
		assertEquals(itemToSave.getAvailable(), actualItem.getAvailable());
	}

	@Test
	void update_whenValidItemUpdateDtoProvidedAndUserIsOwner_thenItemIsUpdated() {
		long itemId = 1L;
		long userId = 1L;
		User owner = User.builder().id(userId).build();
		Item oldItem = Item.builder().id(itemId).name("name").description("description")
				.available(true).owner(owner).build();
		Item newItem = Item.builder().id(itemId).name("name2").description("description")
				.available(true).owner(owner).build();
		ItemUpdateDto itemUpdate = ItemUpdateDto.builder().id(itemId).name("name2")
				.description("description").available(true).ownerId(userId).build();

		when(userService.getById(userId)).thenReturn(UserDtoMapper.toUserDto(owner));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

		when(itemRepository.save(any(Item.class))).thenReturn(newItem);

		ItemDto actualItem = itemService.update(itemUpdate);

		verify(itemRepository).save(any(Item.class));

		assertEquals(newItem.getId(), actualItem.getId());
		assertEquals(newItem.getName(), actualItem.getName());
		assertEquals(newItem.getDescription(), actualItem.getDescription());
	}

	@Test
	void update_whenUserNotOwner_thenThrowAccessDeniedException() {
		long itemId = 1L;
		long userId = 2L;
		User owner = User.builder().id(1L).build();
		User notOwner = User.builder().id(userId).build();
		Item oldItem = Item.builder().id(itemId).name("name").description("description")
				.available(true).owner(owner).build();
		ItemUpdateDto itemUpdate = ItemUpdateDto.builder().id(itemId).name("name2")
				.description("description").available(true).ownerId(userId).build();

		when(userService.getById(userId)).thenReturn(UserDtoMapper.toUserDto(notOwner));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

		assertThrows(AccessDeniedException.class, () -> itemService.update(itemUpdate));
	}

	@Test
	void update_whenMultipleFieldsChanged_thenAllFieldsAreUpdated() {
		long itemId = 1L;
		long userId = 1L;
		String newName = "New Name";
		String newDescription = "New Description";
		boolean newAvailability = false;
		User owner = User.builder().id(userId).build();
		Item oldItem = Item.builder().id(itemId).name("Old Name").description("Old Description")
				.available(true).owner(owner).build();
		Item updatedItem = Item.builder().id(itemId).name(newName).description(newDescription)
				.available(newAvailability).owner(owner).build();
		ItemUpdateDto itemUpdate = ItemUpdateDto.builder().id(itemId).name(newName)
				.description(newDescription).available(newAvailability).ownerId(userId).build();

		when(userService.getById(userId)).thenReturn(UserDtoMapper.toUserDto(owner));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
		when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

		ItemDto actualItem = itemService.update(itemUpdate);

		verify(itemRepository).save(any(Item.class));
		assertEquals(updatedItem.getId(), actualItem.getId());
		assertEquals(updatedItem.getName(), actualItem.getName());
		assertEquals(updatedItem.getDescription(), actualItem.getDescription());
		assertEquals(updatedItem.getAvailable(), actualItem.getAvailable());
	}

	@Test
	void getById_whenValidItemIdAndUserId_thenReturnsItem() {
		long itemId = 1L;
		long userId = 1L;
		UserDto userDto = UserDto.builder().id(userId).build();
		Item item = Item.builder().id(itemId).name("name").description("description")
				.available(true).owner(UserDtoMapper.ofUserDto(userDto)).build();

		when(userService.getById(userId)).thenReturn(userDto);
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
		when(bookingRepository.findTop1ByItemOwnerIdAndStartDateBeforeAndStatusIn(anyLong(), any(), any(), any())).thenReturn(null);
		when(bookingRepository.findTop1ByItemOwnerIdAndStartDateAfterAndStatusIn(anyLong(), any(), any(), any())).thenReturn(null);
		when(commentRepository.findAllByItemId(itemId)).thenReturn(null);

		ItemWithFullInfoDto actualItemWithFullInfoDto = itemService.getById(itemId, userId);

		assertEquals("name", actualItemWithFullInfoDto.getName());
		assertEquals("description", actualItemWithFullInfoDto.getDescription());
	}

	@Test
	void getItemById_whenItemNotFound_thenThrowsNotFoundException() {
		long itemId = 1L;
		long userId = 1L;
		when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));
	}

	@Test
	void getItemById_whenUserIsOwner_thenReturnsItemWithBookings() {
		long itemId = 1L;
		long ownerId = 1L;
		User owner = User.builder().id(ownerId).build();
		Item item = Item.builder().id(itemId).name("Test Item").owner(owner).build();
		Booking lastBooking = Booking.builder().id(1L).item(item).build();
		Booking nextBooking = Booking.builder().id(2L).item(item).build();
		when(bookingRepository.findTop1ByItemOwnerIdAndStartDateBeforeAndStatusIn(anyLong(), any(), any(), any()))
				.thenReturn(lastBooking);
		when(bookingRepository.findTop1ByItemOwnerIdAndStartDateAfterAndStatusIn(anyLong(), any(), any(), any()))
				.thenReturn(nextBooking);
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

		ItemWithFullInfoDto actualItem = itemService.getById(itemId, ownerId);

		assertEquals(item.getId(), actualItem.getId());
		assertEquals(item.getName(), actualItem.getName());
		assertEquals(item.getDescription(), actualItem.getDescription());
		assertEquals(item.getAvailable(), actualItem.getAvailable());
		assertEquals(lastBooking.getId(), actualItem.getLastBooking().getId());
		assertEquals(nextBooking.getId(), actualItem.getNextBooking().getId());
	}

	@Test
	void getItemById_whenUserIsNotOwner_thenReturnsItemWithoutBookings() {
		long itemId = 1L;
		long userId = 1L;
		User owner = User.builder().id(2L).build();
		Item item = Item.builder().id(itemId).name("Test Item").owner(owner).build();

		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

		ItemWithFullInfoDto actualItem = itemService.getById(itemId, userId);

		assertEquals(item.getId(), actualItem.getId());
		assertEquals(item.getName(), actualItem.getName());
		assertEquals(item.getDescription(), actualItem.getDescription());
		assertEquals(item.getAvailable(), actualItem.getAvailable());
		assertNull(actualItem.getLastBooking());
		assertNull(actualItem.getNextBooking());
	}

	@Test
	void getUserItems_whenUserHasNoItem_thenReturnsEmptyList() {
		long userId = 1L;
		UserDto owner = UserDto.builder().id(userId).build();

		List<Item> itemList = new ArrayList<>();
		Pageable page = PageRequest.of(0, 1);

		when(userService.getById(userId)).thenReturn(owner);
		when(itemRepository.findByOwnerId(userId)).thenReturn(itemList);
		when(bookingRepository.findByItemIdAndEndDateBeforeOrderByEndDateDesc(any(), any()))
				.thenReturn(List.of());
		when(bookingRepository.findByItemIdAndStartDateAfterOrderByStartDateAsc(any(), any()))
				.thenReturn(List.of());

		List<ItemWithFullInfoDto> result = itemService.findByOwnerId(userId, page);

		assertEquals(0, result.size());
	}

	@Test
	void getUserItems_whenUserHasMultipleItems_thenReturnsListOfItems() {
		long userId = 1L;
		User user = User.builder().id(userId).build();
		Pageable page = PageRequest.of(0, 1);

		List<Item> itemList = Arrays.asList(
				Item.builder().id(1L).name("Item 1").owner(user).build(),
				Item.builder().id(2L).name("Item 2").owner(user).build(),
				Item.builder().id(3L).name("Item 3").owner(user).build());
		when(userService.getById(userId)).thenReturn(UserDtoMapper.toUserDto(user));
		when(itemRepository.findByOwnerId(userId)).thenReturn(itemList);
		when(bookingRepository.findByItemIdAndEndDateBeforeOrderByEndDateDesc(any(), any()))
				.thenReturn(List.of());
		when(bookingRepository.findByItemIdAndStartDateAfterOrderByStartDateAsc(any(), any()))
				.thenReturn(List.of());

		List<ItemWithFullInfoDto> actualItems = itemService.findByOwnerId(userId, page);

		assertEquals(itemList.size(), actualItems.size());
	}

	@Test
	void getUserItems_whenNoItemsForUser_thenReturnsEmptyList() {
		long userId = 1L;
		User user = User.builder().id(userId).build();
		Pageable page = PageRequest.of(0, 1);

		when(userService.getById(userId)).thenReturn(UserDtoMapper.toUserDto(user));
		when(itemRepository.findByOwnerId(userId)).thenReturn(Collections.emptyList());
		when(bookingRepository.findByItemIdAndEndDateBeforeOrderByEndDateDesc(any(), any()))
				.thenReturn(List.of());
		when(bookingRepository.findByItemIdAndStartDateAfterOrderByStartDateAsc(any(), any()))
				.thenReturn(List.of());

		List<ItemWithFullInfoDto> actualItems = itemService.findByOwnerId(userId, page);

		assertTrue(actualItems.isEmpty());
	}

	@Test
	void searchBy_whenValidUserIdAndText_returnsMatchingItemsDto() {
		long userId = 1L;
		long itemId = 1L;
		String text = "item";
		Pageable page = PageRequest.of(0, 1);
		UserDto user = UserDto.builder().id(userId).build();
		ItemDto itemDto = ItemDto.builder().id(itemId).name("item").build();
		Item item = Item.builder().id(itemId).name("item").build();
		when(userService.getById(userId)).thenReturn(user);
		when(itemRepository.findByNameOrDescriptionContainingAndAvailableTrue(text.toLowerCase(), page)).thenReturn(new PageImpl<>(List.of(item), page, 1));

		List<ItemDto> result = itemService.searchBy(userId, text, page);

		assertEquals(1, result.size());
		assertEquals(itemDto.getId(), result.get(0).getId());
		assertEquals(itemDto.getName(), result.get(0).getName());
	}

		@Test
		void addComment_whenAuthorHadBooking_thenSaveCommentAndReturnResponseDto() {
			long userId = 1L;
			long itemId = 1L;
			String text = "comment";
			UserDto author = UserDto.builder().id(userId).build();
			Item item = Item.builder()
					.id(itemId).name("item").owner(UserDtoMapper.ofUserDto(author)).build();
			Booking booking = Booking.builder().id(1L).item(item).build();

			CommentRequestDto commentRequestDto = CommentRequestDto.builder()
					.authorId(userId).itemId(itemId).text(text).build();
			Comment comment = Comment.builder()
					.author(UserDtoMapper.ofUserDto(author)).item(item).text(text).build();

			when(userService.getById(userId)).thenReturn(author);
			when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
			when(bookingRepository.findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(anyLong(), anyLong(), any(), any()))
					.thenReturn(Optional.of(booking));
			when(commentRepository.save(comment)).thenReturn(comment);

			CommentResponseDto commentResponseDto = itemService.addComment(commentRequestDto);

			assertEquals(commentRequestDto.getText(), commentResponseDto.getText());

			verify(commentRepository).save(comment);
		}

	@Test
	void addComment_whenAuthorDoesNotHaveFinishedBooking_throwNotAvailableException() {
		long userId = 1L;
		long itemId = 1L;
		String text = "comment";
		UserDto author = UserDto.builder().id(userId).build();
		Item item = Item.builder()
				.id(itemId).name("item").owner(UserDtoMapper.ofUserDto(author)).build();

		CommentRequestDto commentRequestDto = CommentRequestDto.builder()
				.authorId(userId).itemId(itemId).text(text).build();

		when(userService.getById(userId)).thenReturn(author);
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
		when(bookingRepository.findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(anyLong(), anyLong(), any(), any()))
				.thenReturn(Optional.empty());

		assertThrows(NotAvailableException.class, () -> itemService.addComment(commentRequestDto));
	}

}