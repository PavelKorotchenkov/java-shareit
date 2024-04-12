package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByBookerId(long bookerId, Sort sort);

	List<Booking> findByBookerIdAndStatus(long bookerId, Status status, Sort sort);

	List<Booking> findByBookerIdAndEndDateBefore(long bookerId, LocalDateTime date, Sort sort);

	List<Booking> findByBookerIdAndStartDateAfter(long bookerId, LocalDateTime date, Sort sort);

	List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfter(long bookerId, LocalDateTime startDate, LocalDateTime endDate, Sort sort);

	List<Booking> findByItemUserId(long id, Sort sort);

	List<Booking> findByItemUserIdAndStatus(long id, Status status, Sort sort);

	List<Booking> findByItemUserIdAndEndDateBefore(long id, LocalDateTime date, Sort sort);

	List<Booking> findByItemUserIdAndStartDateAfter(long id, LocalDateTime date, Sort sort);

	List<Booking> findByItemUserIdAndStartDateBeforeAndEndDateAfter(long id, LocalDateTime startDate, LocalDateTime endDate, Sort sort);

	Booking findTop1ByItemUserIdAndStartDateBeforeAndStatusIn(long id, LocalDateTime date, List<Status> status, Sort sort);

	Booking findTop1ByItemUserIdAndStartDateAfterAndStatusIn(long id, LocalDateTime date, List<Status> status, Sort sort);

	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.endDate < :date ORDER BY b.endDate DESC")
	List<Booking> findByItemIdAndStartDateBeforeOrderByEndDateDesc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date);

	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.startDate > :date ORDER BY b.startDate ASC")
	List<Booking> findByItemIdAndStartDateAfterOrderByStartDateAsc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date);

	Optional<Booking> findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(long itemId, long bookerId, Status status, LocalDateTime date);
}
