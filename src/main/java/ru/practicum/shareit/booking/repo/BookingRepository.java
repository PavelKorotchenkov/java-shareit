package ru.practicum.shareit.booking.repo;

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
	List<Booking> findByBookerIdOrderByStartDateDesc(long bookerId);

	List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(long bookerId, Status status);

	List<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(long bookerId, LocalDateTime date);

	List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(long bookerId, LocalDateTime date);

	List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(long bookerId, LocalDateTime startDate, LocalDateTime endDate);

	List<Booking> findByItemUserIdOrderByStartDateDesc(long id);

	List<Booking> findByItemUserIdAndStatusOrderByStartDateDesc(long id, Status status);

	List<Booking> findByItemUserIdAndEndDateBeforeOrderByEndDateDesc(long id, LocalDateTime date);

	List<Booking> findByItemUserIdAndStartDateAfterOrderByStartDateDesc(long id, LocalDateTime date);

	List<Booking> findByItemUserIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(long id, LocalDateTime startDate, LocalDateTime endDate);

	Booking findTop1ByItemUserIdAndStartDateBeforeAndStatusInOrderByEndDateDesc(long id, LocalDateTime date, List<Status> status);

	Booking findTop1ByItemUserIdAndStartDateAfterAndStatusInOrderByStartDateAsc(long id, LocalDateTime date, List<Status> status);

	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.endDate < :date ORDER BY b.endDate DESC")
	List<Booking> findByItemIdAndStartDateBeforeOrderByEndDateDesc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date);

	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.startDate > :date ORDER BY b.startDate ASC")
	List<Booking> findByItemIdAndStartDateAfterOrderByStartDateAsc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date);

	Optional<Booking> findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(long itemId, long bookerId, Status status, LocalDateTime date);
}
