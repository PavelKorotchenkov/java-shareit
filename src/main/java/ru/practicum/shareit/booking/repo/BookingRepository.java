package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	Page<Booking> findByBookerId(long bookerId, Pageable pageable);

	Page<Booking> findByBookerIdAndStatus(long bookerId, Status status, Pageable pageable);

	Page<Booking> findByBookerIdAndEndDateBefore(long bookerId, LocalDateTime date, Pageable pageable);

	Page<Booking> findByBookerIdAndStartDateAfter(long bookerId, LocalDateTime date, Pageable pageable);

	Page<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfter(long bookerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	Page<Booking> findByItemUserIdAndStatus(long id, Status status, Pageable pageable);

	Page<Booking> findByItemUserIdAndEndDateBefore(long id, LocalDateTime date, Pageable pageable);

	Page<Booking> findByItemUserIdAndStartDateAfter(long id, LocalDateTime date, Pageable pageable);

	Page<Booking> findByItemUserIdAndStartDateBeforeAndEndDateAfter(long id, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	Booking findTop1ByItemUserIdAndStartDateBeforeAndStatusIn(long id, LocalDateTime date, List<Status> status, Sort sort);

	Booking findTop1ByItemUserIdAndStartDateAfterAndStatusIn(long id, LocalDateTime date, List<Status> status, Sort sort);

	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.endDate < :date ORDER BY b.endDate DESC")
	Page<Booking> findByItemIdAndEndDateBeforeOrderByEndDateDesc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date, Pageable pageable);

	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.startDate > :date ORDER BY b.startDate ASC")
	Page<Booking> findByItemIdAndStartDateAfterOrderByStartDateAsc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date, Pageable pageable);

	Optional<Booking> findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(long itemId, long bookerId, Status status, LocalDateTime date);

	Page<Booking> findByItemUserId(long userId, Pageable pageable);

}
