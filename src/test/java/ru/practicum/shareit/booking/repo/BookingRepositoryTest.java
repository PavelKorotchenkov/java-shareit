package ru.practicum.shareit.booking.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;
	private User testBookerUser;
	private User testOwnerUser;
	private Item testItem;


	@BeforeEach
	void setUp_createUserAndItem() {
		testBookerUser = userRepository.save(User.builder()
				.email("booker@mail.ru")
				.name("booker")
				.build());

		testOwnerUser = userRepository.save(User.builder()
				.email("user@mail.ru")
				.name("user")
				.build());

		testItem = itemRepository.save(Item.builder()
				.name("item")
				.description("description")
				.owner(testOwnerUser)
				.available(true)
				.build());
	}

	@Test
	void findByBookerId_whenFindByValidBookerId_thenReturnBooking() {
		Booking booking = Booking.builder()
				.startDate(LocalDateTime.of(2024, 1, 4, 1, 1, 1))
				.endDate(LocalDateTime.of(2024, 1, 5, 1, 1, 1))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();
		Pageable pageable = PageRequest.of(0, 1);
		bookingRepository.save(booking);
		Page<Booking> bookingPage = bookingRepository.findByBookerId(testBookerUser.getId(), pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(booking, bookingPage.getContent().get(0));
	}

	@Test
	void findByBookerIdAndStatus_whenFindByValidBookerIdAndStatusWaiting_thenReturnBookingWithStatusWaiting() {
		Booking bookingApproved = Booking.builder()
				.startDate(LocalDateTime.of(2024, 1, 4, 1, 1, 1))
				.endDate(LocalDateTime.of(2024, 1, 5, 1, 1, 1))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();
		Booking bookingWaiting = Booking.builder()
				.startDate(LocalDateTime.of(2024, 5, 1, 1, 1))
				.endDate(LocalDateTime.of(2024, 5, 2, 1, 1, 1))
				.item(testItem).booker(testBookerUser).status(Status.WAITING).build();
		Pageable pageable = PageRequest.of(0, 1);
		bookingRepository.save(bookingApproved);
		bookingRepository.save(bookingWaiting);
		Page<Booking> bookingPage = bookingRepository.findByBookerIdAndStatus(testBookerUser.getId(), Status.WAITING, pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(bookingWaiting, bookingPage.getContent().get(0));
		assertNotEquals(bookingApproved, bookingPage.getContent().get(0));
	}

	@Test
	void findByBookerIdAndEndDateBefore_whenTwoBookingsInDB_thenReturnBookingThatEndsBeforeTestTime() {
		Booking bookingEndFirst = Booking.builder()
				.startDate(LocalDateTime.of(2024, 4, 10, 1, 1, 1))
				.endDate(LocalDateTime.of(2024, 4, 12, 1, 1, 1))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();
		Booking bookingEndSecond = Booking.builder()
				.startDate(LocalDateTime.of(2024, 4, 12, 1, 1))
				.endDate(LocalDateTime.of(2024, 4, 14, 1, 1, 1))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();
		LocalDateTime testTime = LocalDateTime.of(2024, 4, 13, 1, 1, 1);
		Pageable pageable = PageRequest.of(0, 1);
		bookingRepository.save(bookingEndFirst);
		bookingRepository.save(bookingEndSecond);
		Page<Booking> bookingPage = bookingRepository.findByBookerIdAndEndDateBefore(testBookerUser.getId(), testTime, pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(bookingEndFirst, bookingPage.getContent().get(0));
		assertNotEquals(bookingEndSecond, bookingPage.getContent().get(0));
	}

	@Test
	void findByBookerIdAndStartDateAfter_whenTwoBookingsInDB_thenReturnBookingThatStartsAfterTestTime() {
		Booking bookingStartFirst = Booking.builder()
				.startDate(LocalDateTime.of(2024, 4, 10, 1, 1, 1))
				.endDate(LocalDateTime.of(2024, 4, 12, 1, 1, 1))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();
		Booking bookingStartSecond = Booking.builder()
				.startDate(LocalDateTime.of(2024, 4, 12, 1, 1))
				.endDate(LocalDateTime.of(2024, 4, 14, 1, 1, 1))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();
		LocalDateTime testTime = LocalDateTime.of(2024, 4, 11, 1, 1, 1);
		Pageable pageable = PageRequest.of(0, 1);
		bookingRepository.save(bookingStartFirst);
		bookingRepository.save(bookingStartSecond);
		Page<Booking> bookingPage = bookingRepository.findByBookerIdAndStartDateAfter(testBookerUser.getId(), testTime, pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(bookingStartSecond, bookingPage.getContent().get(0));
		assertNotEquals(bookingStartFirst, bookingPage.getContent().get(0));
	}

	@Test
	void findByBookerIdAndStartDateBeforeAndEndDateAfter_whenTwoBookingsInDBThatStartBeforeTodayAndEndAfterToday_thenReturnBothBookings() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(1))
				.endDate(LocalDateTime.now().plusDays(1))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(2))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testBookerUser).status(Status.WAITING).build();
		LocalDateTime testTime = LocalDateTime.now();
		Pageable pageable = PageRequest.of(0, 2);
		bookingRepository.save(bookingFirst);
		bookingRepository.save(bookingSecond);
		Page<Booking> bookingPage = bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(testBookerUser.getId(), testTime, testTime, pageable);
		assertEquals(2, bookingPage.getTotalElements());
		assertEquals(bookingFirst, bookingPage.getContent().get(0));
		assertEquals(bookingSecond, bookingPage.getContent().get(1));
	}

	@Test
	void findByItemUserIdAndStatus_whenInvoked_thenReturnBookingWithAskedStatus() {
		Booking booking = Booking.builder()
				.startDate(LocalDateTime.of(2024, 4, 4, 1, 1, 1))
				.endDate(LocalDateTime.of(2024, 5, 5, 1, 1, 1))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Pageable pageable = PageRequest.of(0, 1);
		bookingRepository.save(booking);
		Page<Booking> bookingPage = bookingRepository.findByItemOwnerIdAndStatus(testOwnerUser.getId(), Status.APPROVED, pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(booking, bookingPage.getContent().get(0));
	}

	@Test
	void findByItemUserIdAndEndDateBefore_whenInvoked_thenReturnBookingThatAlreadyEnded() {
		Booking bookingEnded = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(2))
				.endDate(LocalDateTime.now().minusDays(1))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingCurrent = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(2))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.WAITING).build();
		LocalDateTime testTime = LocalDateTime.now();
		Pageable pageable = PageRequest.of(0, 2);
		bookingRepository.save(bookingEnded);
		bookingRepository.save(bookingCurrent);
		Page<Booking> bookingPage = bookingRepository.findByItemOwnerIdAndEndDateBefore(testOwnerUser.getId(), testTime, pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(bookingEnded, bookingPage.getContent().get(0));
	}

	@Test
	void findByItemUserIdAndStartDateAfter_whenInvoked_thenReturnBookingThatWillStartTomorrow() {
		Booking bookingStartTomorrow = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingCurrent = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(2))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.WAITING).build();
		LocalDateTime testTime = LocalDateTime.now();
		Pageable pageable = PageRequest.of(0, 2);
		bookingRepository.save(bookingStartTomorrow);
		bookingRepository.save(bookingCurrent);
		Page<Booking> bookingPage = bookingRepository.findByItemOwnerIdAndStartDateAfter(testOwnerUser.getId(), testTime, pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(bookingStartTomorrow, bookingPage.getContent().get(0));
	}

	@Test
	void findByItemUserIdAndStartDateBeforeAndEndDateAfter_whenInvoked_thenReturnCurrentBooking() {
		Booking bookingCurrent = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(1))
				.endDate(LocalDateTime.now().plusDays(1))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingPast = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(2))
				.endDate(LocalDateTime.now().minusDays(1))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingFuture = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		LocalDateTime testTime = LocalDateTime.now();
		Pageable pageable = PageRequest.of(0, 3);
		bookingRepository.save(bookingCurrent);
		bookingRepository.save(bookingPast);
		bookingRepository.save(bookingFuture);
		Page<Booking> bookingPage = bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(testOwnerUser.getId(), testTime, testTime, pageable);
		assertEquals(1, bookingPage.getTotalElements());
		assertEquals(bookingCurrent, bookingPage.getContent().get(0));
	}

	@Test
	void findTop1ByItemUserIdAndStartDateBeforeAndStatusIn_whenInvoked_thenReturnClosestBookingByStartDateBeforeNow() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(3))
				.endDate(LocalDateTime.now().plusDays(1))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(2))
				.endDate(LocalDateTime.now().minusDays(1))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingThird = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();

		LocalDateTime testTime = LocalDateTime.now();

		bookingRepository.save(bookingFirst);
		bookingRepository.save(bookingSecond);
		bookingRepository.save(bookingThird);

		Booking booking =
				bookingRepository.findTop1ByItemOwnerIdAndStartDateBeforeAndStatusIn(testOwnerUser.getId(),
						testTime, List.of(Status.APPROVED), Sort.by(Sort.Direction.DESC, "StartDate"));
		assertEquals(bookingThird.getId(), booking.getId());
	}

	@Test
	void findTop1ByItemUserIdAndStartDateAfterAndStatusIn_whenInvoked_thenReturnClosestBookingByStartDateAfterNow() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(2))
				.endDate(LocalDateTime.now().plusDays(3))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingThird = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(3))
				.endDate(LocalDateTime.now().plusDays(4))
				.item(testItem).booker(testOwnerUser).status(Status.WAITING).build();

		LocalDateTime testTime = LocalDateTime.now();

		bookingRepository.save(bookingFirst);
		bookingRepository.save(bookingSecond);
		bookingRepository.save(bookingThird);

		Booking booking =
				bookingRepository.findTop1ByItemOwnerIdAndStartDateAfterAndStatusIn(testOwnerUser.getId(),
						testTime, List.of(Status.APPROVED), Sort.by(Sort.Direction.ASC, "StartDate"));
		assertEquals(bookingFirst.getId(), booking.getId());
	}

	@Test
	void findByItemIdAndEndDateBeforeOrderByEndDateDesc_whenInvoked_thenReturnSortedBookings() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(4))
				.endDate(LocalDateTime.now().minusDays(3))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(3))
				.endDate(LocalDateTime.now().minusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingThird = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(2))
				.endDate(LocalDateTime.now().minusDays(1))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();

		LocalDateTime testTime = LocalDateTime.now();

		bookingRepository.save(bookingFirst);
		bookingRepository.save(bookingSecond);
		bookingRepository.save(bookingThird);

		List<Booking> bookings =
				bookingRepository.findByItemIdAndEndDateBeforeOrderByEndDateDesc(Set.of(testItem.getId()),
						testTime);
		assertEquals(3, bookings.size());
		assertEquals(bookingThird, bookings.get(0));
		assertEquals(bookingSecond, bookings.get(1));
		assertEquals(bookingFirst, bookings.get(2));
	}

	@Test
	void findByItemIdAndStartDateAfterOrderByStartDateAsc_whenInvoked_thenReturnSortedBookings() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(2))
				.endDate(LocalDateTime.now().plusDays(3))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingThird = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(3))
				.endDate(LocalDateTime.now().plusDays(4))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();

		LocalDateTime testTime = LocalDateTime.now();

		bookingRepository.save(bookingFirst);
		bookingRepository.save(bookingSecond);
		bookingRepository.save(bookingThird);

		List<Booking> bookings =
				bookingRepository.findByItemIdAndStartDateAfterOrderByStartDateAsc(Set.of(testItem.getId()),
						testTime);
		assertEquals(3, bookings.size());
		assertEquals(bookingFirst, bookings.get(0));
		assertEquals(bookingSecond, bookings.get(1));
		assertEquals(bookingThird, bookings.get(2));
	}

	@Test
	void findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore_whenExistOneSuchBooking_thenReturnOptionalOfThatBooking() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(4))
				.endDate(LocalDateTime.now().minusDays(3))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();

		LocalDateTime testTime = LocalDateTime.now();

		bookingRepository.save(bookingFirst);

		Optional<Booking> booking =
				bookingRepository.findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(testItem.getId(),
						testBookerUser.getId(), Status.APPROVED, testTime);

		assertEquals(bookingFirst.getId(), booking.get().getId());
	}

	@Test
	void findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore_whenNoSuchBooking_thenReturnEmptyOptional() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().minusDays(4))
				.endDate(LocalDateTime.now().plusDays(3))
				.item(testItem).booker(testBookerUser).status(Status.APPROVED).build();

		LocalDateTime testTime = LocalDateTime.now();

		bookingRepository.save(bookingFirst);

		Optional<Booking> booking =
				bookingRepository.findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(testItem.getId(),
						testBookerUser.getId(), Status.APPROVED, testTime);

		assertTrue(booking.isEmpty());
	}

	@Test
	void findByItemUserId_whenInvoked_thenReturnAllBookingsOfOwner() {
		Booking bookingFirst = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.item(testItem).booker(testOwnerUser).status(Status.APPROVED).build();
		Booking bookingSecond = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(2))
				.endDate(LocalDateTime.now().plusDays(3))
				.item(testItem).booker(testOwnerUser).status(Status.WAITING).build();
		Booking bookingThird = Booking.builder()
				.startDate(LocalDateTime.now().plusDays(3))
				.endDate(LocalDateTime.now().plusDays(4))
				.item(testItem).booker(testOwnerUser).status(Status.REJECTED).build();

		Pageable pageable = PageRequest.of(0, 3);

		bookingRepository.save(bookingFirst);
		bookingRepository.save(bookingSecond);
		bookingRepository.save(bookingThird);

		Page<Booking> bookingPage =
				bookingRepository.findByItemOwnerId(testOwnerUser.getId(), pageable);
		assertEquals(3, bookingPage.getTotalElements());
		assertEquals(bookingFirst, bookingPage.getContent().get(0));
		assertEquals(bookingSecond, bookingPage.getContent().get(1));
		assertEquals(bookingThird, bookingPage.getContent().get(2));
	}

	@AfterEach
	void cleanUp() {
		itemRepository.deleteAll();
		bookingRepository.deleteAll();
		userRepository.deleteAll();
	}
}