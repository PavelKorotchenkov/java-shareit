package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
	@Mock
	private ItemService itemService;
	@InjectMocks
	private ItemController itemController;
	public static final long X_SHARER_USER_ID = 1L;

	@Test
	void addItem_whenItemCreateDtoInRequest_thenReturnItemDto() {
		long userId = 1L;
		ItemCreateDto itemCreateDtoToSave = ItemCreateDto.builder()
				.name("name")
				.description("description")
				.available(true)
				.ownerId(userId)
				.requestId(1L).build();

		ItemDto expectedResponse = ItemDto.builder()
				.id(1L)
				.name("name")
				.description("description")
				.available(true)
				.ownerId(userId)
				.requestId(1L).build();

		when(itemService.add(itemCreateDtoToSave)).thenReturn(expectedResponse);

		ResponseEntity<ItemDto> response = itemController.addItem(userId, itemCreateDtoToSave);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(Objects.requireNonNull(response.getBody()).getName(), "name");
		assertEquals(Objects.requireNonNull(response.getBody()).getDescription(), "description");
	}

	@Test
	void updateItem_whenItemUpdateDtoInRequest_thenReturnItemDtoWithIdAndOwnerIdAndRequestId() {
		long ownerId = 1L;
		long itemId = 1L;
		ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
				.name("name upd")
				.description("description upd")
				.available(true).build();

		ItemDto expectedResponse = ItemDto.builder()
				.id(itemId)
				.name("name upd")
				.description("description upd")
				.available(true)
				.ownerId(ownerId)
				.requestId(null).build();

		when(itemService.update(itemUpdateDto)).thenReturn(expectedResponse);

		ResponseEntity<ItemDto> response = itemController.updateItem(ownerId, itemId, itemUpdateDto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	void getItem_whenItemExists_thenReturnItemWithFullInfoDto() {
		long itemId = 1L;
		long userId = 1L;
		ItemWithFullInfoDto expectedItem = ItemWithFullInfoDto.builder()
				.id(itemId)
				.name("name")
				.description("description")
				.available(true)
				.lastBooking(null)
				.nextBooking(null)
				.comments(new ArrayList<>())
				.requestId(null).build();

		when(itemService.getById(itemId, userId)).thenReturn(expectedItem);

		ResponseEntity<ItemWithFullInfoDto> response = itemController.getItem(itemId, userId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedItem, response.getBody());
	}

	@Test
	void findAllByOwnerId_whenValidParameters_thenReturnStatusOkWithListWithOneItemWithFullInfoDtoInBody() {
		long ownerId = 1L;
		int from = 0;
		int size = 1;
		ItemWithFullInfoDto expectedItem = ItemWithFullInfoDto.builder()
				.id(1L)
				.name("name")
				.description("description")
				.available(true)
				.lastBooking(null)
				.nextBooking(null)
				.comments(new ArrayList<>())
				.requestId(null).build();
		List<ItemWithFullInfoDto> expectedList = List.of(expectedItem);
		PageRequest page = PageRequest.of(from, size);
		when(itemService.getUserItems(ownerId, page)).thenReturn(expectedList);
		ResponseEntity<List<ItemWithFullInfoDto>> response = itemController.findAllByOwnerId(ownerId, from, size);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedList, response.getBody());
	}

	@Test
	void searchBy() {
		long userId = 1L;
		String text = "item";
		int from = 0;
		int size = 1;
		ItemDto dto = ItemDto.builder()
				.id(1L)
				.name("name")
				.description("item description")
				.available(true)
				.ownerId(2L)
				.requestId(1L).build();
		List<ItemDto> expectedList = List.of(dto);
		PageRequest page = PageRequest.of(from, size);
		when(itemService.searchBy(userId, text, page)).thenReturn(expectedList);
		ResponseEntity<List<ItemDto>> response = itemController.searchBy(userId, text, from, size);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedList, response.getBody());
	}

	@Test
	void addComment_whenCommentRequestDtoInRequest_thenReturnStatusOkWithCommentResponseDtoInBody() {
		long itemId = 1L;
		String comment = "comment";
		String created = "2024-04-18T15:10:10";
		CommentRequestDto commentRequestDto = CommentRequestDto.builder()
				.text(comment)
				.itemId(itemId)
				.authorId(2L)
				.build();
		CommentResponseDto expectedComment = CommentResponseDto.builder()
				.id(1L)
				.text(comment)
				.authorName("name")
				.created(created)
				.build();
		when(itemService.addComment(commentRequestDto)).thenReturn(expectedComment);
		ResponseEntity<CommentResponseDto> response = itemController.addComment(X_SHARER_USER_ID, itemId, commentRequestDto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedComment, response.getBody());
	}
}