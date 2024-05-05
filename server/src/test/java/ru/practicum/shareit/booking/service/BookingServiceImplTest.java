package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.BookingDateException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithFullInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ItemService itemService;

	@Mock
	private UserService userService;

	@Captor
	private ArgumentCaptor<Item> argumentCaptor;

	@InjectMocks
	private BookingServiceImpl bookingService;

	@Test
	void add_whenAllValidFields_thenReturnBooking() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		LocalDateTime start = LocalDateTime.now().plusDays(1);
		LocalDateTime end = LocalDateTime.now().plusDays(2);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String formattedDateTimeStart = start.format(formatter);
		String formattedDateTimeEnd = end.format(formatter);
		BookingRequestDto bookingToSave = BookingRequestDto.builder()
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.itemId(item.getId())
				.build();
		Booking bookingEntity = Booking.builder().id(1L).startDate(start).endDate(end).build();
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		when(userService.getById(anyLong())).thenReturn(UserDtoMapper.toUserDto(booker));
		when(bookingRepository.save(any())).thenReturn(bookingEntity);

		BookingResponseDto actualBooking = bookingService.add(booker.getId(), bookingToSave);
		assertEquals(formattedDateTimeStart, actualBooking.getStart());
		assertEquals(formattedDateTimeEnd, actualBooking.getEnd());
	}

	@Test
	void add_whenBookerIdEqualsItemOwnerId_thenReturnNotFoundException() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		BookingRequestDto bookingToSave = BookingRequestDto.builder()
				.start("2024-04-23T10:10:10")
				.end("2024-04-24T10:10:10")
				.itemId(item.getId())
				.build();
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		assertThrows(NotFoundException.class, () -> bookingService.add(owner.getId(), bookingToSave));
	}

	@Test
	void add_whenItemNotAvailable_thenReturnNotAvailableException() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(false).owner(owner).build();
		BookingRequestDto bookingToSave = BookingRequestDto.builder()
				.start("2024-04-23T10:10:10")
				.end("2024-04-24T10:10:10")
				.itemId(item.getId())
				.build();
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		assertThrows(NotAvailableException.class, () -> bookingService.add(booker.getId(), bookingToSave));
	}

	/*@Test
	void add_whenStartBookingTimeIsNull_thenReturnBookingDateException() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		BookingRequestDto bookingToSave = BookingRequestDto.builder()
				.start(null)
				.end("2024-04-24T10:10:10")
				.itemId(item.getId())
				.build();
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		assertThrows(BookingDateException.class, () -> bookingService.add(booker.getId(), bookingToSave));
	}*/

	@Test
	void add_whenStartDateAfterEndDate_thenReturnBookingDateException() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		BookingRequestDto bookingToSave = BookingRequestDto.builder()
				.start("2024-04-23T10:10:10")
				.end("2024-03-24T10:10:10")
				.itemId(item.getId())
				.build();
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		assertThrows(BookingDateException.class, () -> bookingService.add(booker.getId(), bookingToSave));
	}

	@Test
	void approve_whenOwnerApprovedBooking_thenReturnBookingResponseWithApprovedStatus() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		ItemWithFullInfoDto itemWithFullInfoDto = ItemWithFullInfoDto.builder()
				.id(1L).name("item").description("description")
				.available(true).lastBooking(null).nextBooking(null).comments(null).requestId(null).build();
		Booking bookingEntity = Booking.builder().id(1L)
				.startDate(LocalDateTime.of(2024, 4, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 5, 23, 10, 10, 10))
				.item(item).booker(booker).status(Status.WAITING).build();

		when(userService.getById(owner.getId())).thenReturn(UserDtoMapper.toUserDto(owner));
		when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));
		when(itemService.getById(item.getId(), owner.getId())).thenReturn(itemWithFullInfoDto);
		when(itemService.update(any())).thenReturn(ItemDto.builder().build());
		when(bookingRepository.save(bookingEntity)).thenReturn(bookingEntity);

		BookingResponseDto actualBooking = bookingService.approve(owner.getId(), bookingEntity.getId(), true);

		assertEquals(Status.APPROVED, actualBooking.getStatus());
	}

	@Test
	void approve_whenOwnerDidNotApproveBooking_thenReturnBookingResponseWithRejectedStatus() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		ItemWithFullInfoDto itemWithFullInfoDto = ItemWithFullInfoDto.builder()
				.id(1L).name("item").description("description")
				.available(true).lastBooking(null).nextBooking(null).comments(null).requestId(null).build();
		Booking bookingEntity = Booking.builder().id(1L)
				.startDate(LocalDateTime.of(2024, 4, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 5, 23, 10, 10, 10))
				.item(item).booker(booker).status(Status.WAITING).build();

		when(userService.getById(owner.getId())).thenReturn(UserDtoMapper.toUserDto(owner));
		when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));
		when(itemService.getById(item.getId(), owner.getId())).thenReturn(itemWithFullInfoDto);
		when(itemService.update(any())).thenReturn(ItemDto.builder().build());
		when(bookingRepository.save(bookingEntity)).thenReturn(bookingEntity);

		BookingResponseDto actualBooking = bookingService.approve(owner.getId(), bookingEntity.getId(), false);

		assertEquals(Status.REJECTED, actualBooking.getStatus());
	}

	@Test
	void approve_whenOwnerIdAndUserIdNotEqual_thenThrowNotFoundException() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingEntity = Booking.builder().id(1L)
				.startDate(LocalDateTime.of(2024, 4, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 5, 23, 10, 10, 10))
				.item(item).booker(owner).status(Status.WAITING).build();

		when(userService.getById(booker.getId())).thenReturn(UserDtoMapper.toUserDto(booker));
		when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

		assertThrows(NotFoundException.class,
				() -> bookingService.approve(booker.getId(), bookingEntity.getId(), false));
	}

	@Test
	void approve_whenStatusALreadyApproved_thenThrowNotAvailableException() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingEntity = Booking.builder().id(1L)
				.startDate(LocalDateTime.of(2024, 4, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 5, 23, 10, 10, 10))
				.item(item).booker(owner).status(Status.APPROVED).build();

		when(userService.getById(owner.getId())).thenReturn(UserDtoMapper.toUserDto(owner));
		when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

		assertThrows(NotAvailableException.class,
				() -> bookingService.approve(owner.getId(), bookingEntity.getId(), false));
	}

	@Test
	void getInfoById_whenValidBookerIdAndItemOwnerId_thenReturnBookingResponseDto() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingEntity = Booking.builder().id(1L)
				.startDate(LocalDateTime.of(2024, 4, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 5, 23, 10, 10, 10))
				.item(item).booker(booker).status(Status.APPROVED).build();
		BookingResponseDto expectedBookingResponseDto = BookingDtoMapper.toBookingResponseDto(bookingEntity);
		when(userService.getById(booker.getId())).thenReturn(UserDtoMapper.toUserDto(booker));
		when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

		BookingResponseDto actualBookingResponseDto = bookingService.getInfoById(booker.getId(), bookingEntity.getId());
		assertEquals(expectedBookingResponseDto.getBooker().getId(), actualBookingResponseDto.getBooker().getId());
	}

	@Test
	void getInfoById_whenNotValidBookerIdAndOwnerId_thenThrowNotFoundException() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		User someone = User.builder().id(3L).name("someone").email("sm@mail.ru").build();
		Booking bookingEntity = Booking.builder().id(1L)
				.startDate(LocalDateTime.of(2024, 4, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 5, 23, 10, 10, 10))
				.item(item).booker(booker).status(Status.APPROVED).build();
		when(userService.getById(any())).thenReturn(UserDtoMapper.toUserDto(someone));
		when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

		assertThrows(NotFoundException.class,
				() -> bookingService.getInfoById(someone.getId(), bookingEntity.getId()));
	}

	@Test
	void getAllBookings_whenStateAll_thenReturnAllBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.now().minusDays(3))
				.endDate(LocalDateTime.now().minusDays(2))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.id(2L)
				.startDate(LocalDateTime.now().minusDays(1))
				.endDate(LocalDateTime.now().plusDays(1))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Booking bookingThird = Booking.builder()
				.id(3L)
				.startDate(LocalDateTime.now().plusDays(2))
				.endDate(LocalDateTime.now().plusDays(3))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Pageable page = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "StartDate"));

		when(bookingRepository.findByBookerId(booker.getId(), page))
				.thenReturn(new PageImpl<>(List.of(
						bookingThird,
						bookingSecond,
						bookingFirst), page, 3));

		List<BookingResponseDto> actualList = bookingService.getAllBookings(booker.getId(), BookingState.ALL, page);

		assertEquals(3, actualList.get(0).getId());
		assertEquals(2, actualList.get(1).getId());
		assertEquals(1, actualList.get(2).getId());
	}

	@Test
	void getAllBookings_whenStatePast_thenReturnPastBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.now().minusDays(3))
				.endDate(LocalDateTime.now().minusDays(2))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));

		when(bookingRepository.findByBookerIdAndEndDateBefore(
				eq(booker.getId()),
				ArgumentMatchers.any(LocalDateTime.class),
				eq(page)))
				.thenReturn(new PageImpl<>(List.of(bookingFirst), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllBookings(booker.getId(), BookingState.PAST, page);
		assertEquals(1, actualList.get(0).getId());
	}

	@Test
	void getAllBookings_whenStateCurrent_thenReturnCurrentBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		LocalDateTime now = LocalDateTime.now();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(now.minusDays(3))
				.endDate(now.plusDays(2))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));

		when(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(
				eq(booker.getId()),
				ArgumentMatchers.any(LocalDateTime.class),
				ArgumentMatchers.any(LocalDateTime.class),
				eq(page)))
				.thenReturn(new PageImpl<>(List.of(bookingFirst), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllBookings(booker.getId(), BookingState.CURRENT, page);
		assertEquals(1, actualList.get(0).getId());
	}

	@Test
	void getAllBookings_whenStateFuture_thenReturnFutureBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		LocalDateTime now = LocalDateTime.now();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(now.plusDays(1))
				.endDate(now.plusDays(2))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));

		when(bookingRepository.findByBookerIdAndStartDateAfter(
				eq(booker.getId()),
				ArgumentMatchers.any(LocalDateTime.class),
				eq(page)))
				.thenReturn(new PageImpl<>(List.of(bookingFirst), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllBookings(booker.getId(), BookingState.FUTURE, page);
		assertEquals(1, actualList.get(0).getId());
	}

	@Test
	void getAllBookings_whenStateWaiting_thenReturnWaitingBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		User booker = User.builder().id(2L).email("booker@mail.ru").name("booker").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		LocalDateTime now = LocalDateTime.now();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(now.plusDays(1))
				.endDate(now.plusDays(2))
				.item(item).booker(owner).status(Status.WAITING).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));

		when(bookingRepository.findByBookerIdAndStatus(booker.getId(), Status.WAITING, page))
				.thenReturn(new PageImpl<>(List.of(bookingFirst), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllBookings(booker.getId(), BookingState.WAITING, page);
		assertEquals(1, actualList.get(0).getId());
	}

	@Test
	void getAllOwnerBookings_whenStateAll_thenReturnAllOwnerBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.now().minusDays(3))
				.endDate(LocalDateTime.now().minusDays(2))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.id(2L)
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(item).booker(owner).status(Status.WAITING).build();
		Pageable page = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "StartDate"));
		when(bookingRepository.findByItemOwnerId(owner.getId(), page))
				.thenReturn(new PageImpl<>(List.of(bookingFirst, bookingSecond), page, 2));

		List<BookingResponseDto> actualList = bookingService.getAllOwnerBookings(owner.getId(), BookingState.ALL, page);

		assertEquals(1, actualList.get(0).getId());
		assertEquals(2, actualList.get(1).getId());
		verify(userService, times(1)).getById(anyLong());
	}

	@Test
	void getAllOwnerBookings_whenStatePast_thenReturnPastOwnerBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.of(2024, 4, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 4, 25, 10, 10, 10))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.id(2L)
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(item).booker(owner).status(Status.WAITING).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));
		when(bookingRepository.findByItemOwnerIdAndEndDateBefore(
				eq(owner.getId()),
				ArgumentMatchers.any(LocalDateTime.class),
				eq(page)))
				.thenReturn(new PageImpl<>(List.of(bookingFirst), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllOwnerBookings(owner.getId(), BookingState.PAST, page);

		assertEquals(1, actualList.get(0).getId());
		verify(userService, times(1)).getById(anyLong());
	}

	@Test
	void getAllOwnerBookings_whenStateCurrent_thenReturnCurrentOwnerBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.of(2024, 1, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 2, 25, 10, 10, 10))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.id(2L)
				.startDate(LocalDateTime.now().minusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));
		when(bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(
				eq(owner.getId()),
				ArgumentMatchers.any(LocalDateTime.class),
				ArgumentMatchers.any(LocalDateTime.class),
				eq(page)))
				.thenReturn(new PageImpl<>(List.of(bookingSecond), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllOwnerBookings(owner.getId(), BookingState.CURRENT, page);

		assertEquals(2, actualList.get(0).getId());
		verify(userService, times(1)).getById(anyLong());
	}

	@Test
	void getAllOwnerBookings_whenStateFuture_thenReturnFutureOwnerBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.of(2024, 1, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 2, 25, 10, 10, 10))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.id(2L)
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));
		when(bookingRepository.findByItemOwnerIdAndStartDateAfter(
				eq(owner.getId()),
				ArgumentMatchers.any(LocalDateTime.class),
				eq(page)))
				.thenReturn(new PageImpl<>(List.of(bookingSecond), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllOwnerBookings(owner.getId(), BookingState.FUTURE, page);

		assertEquals(2, actualList.get(0).getId());
		verify(userService, times(1)).getById(anyLong());
	}

	@Test
	void getAllOwnerBookings_whenStateApproved_thenReturnApprovedOwnerBookings() {
		User owner = User.builder().id(1L).email("owner@mail.ru").name("owner").build();
		Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).build();
		Booking bookingFirst = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.of(2024, 1, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 2, 25, 10, 10, 10))
				.item(item).booker(owner).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.id(1L)
				.startDate(LocalDateTime.of(2024, 2, 23, 10, 10, 10))
				.endDate(LocalDateTime.of(2024, 3, 25, 10, 10, 10))
				.item(item).booker(owner).status(Status.REJECTED).build();
		Booking bookingThird = Booking.builder()
				.id(2L)
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(item).booker(owner).status(Status.WAITING).build();
		Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "StartDate"));
		when(bookingRepository.findByItemOwnerIdAndStatus(
				eq(owner.getId()),
				eq(Status.valueOf(BookingState.APPROVED.name())),
				eq(page)))
				.thenReturn(new PageImpl<>(List.of(bookingFirst), page, 1));

		List<BookingResponseDto> actualList = bookingService.getAllOwnerBookings(owner.getId(), BookingState.APPROVED, page);

		assertEquals(1, actualList.get(0).getId());
		verify(userService, times(1)).getById(anyLong());
	}
}