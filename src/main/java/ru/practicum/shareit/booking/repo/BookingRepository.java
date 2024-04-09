package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByBookerIdOrderByStartDateDesc(long bookerId);

	List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(long bookerId, Status status);

	List<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(long bookerId, LocalDateTime date);

	List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(long bookerId, LocalDateTime date);

	List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(long bookerId, LocalDateTime startDate, LocalDateTime endDate);

	List<Booking> findByItemUserIdOrderByStartDateDesc(long id);
	List<Booking> findByItemUserIdAndStatusOrderByStartDateDesc(long id, Status state);
	List<Booking> findByItemUserIdAndEndDateBeforeOrderByEndDateDesc(long id, LocalDateTime date); //past
	List<Booking> findByItemUserIdAndStartDateAfterOrderByStartDateDesc(long id, LocalDateTime date); //future desc

	List<Booking> findByItemUserIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(long id, LocalDateTime startDate, LocalDateTime endDate);

	Booking findTop1ByItemUserIdAndEndDateBeforeOrderByEndDateDesc(long id, LocalDateTime date);
	Booking findTop1ByItemUserIdAndStartDateAfterOrderByStartDateAsc(long id, LocalDateTime date);

	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.endDate < :date ORDER BY b.endDate DESC")
	List<Booking> findByItemIdAndEndDateBeforeOrderByEndDateDesc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date); //past desc by item
	@Query("SELECT b FROM Booking b WHERE b.item.id IN :id AND b.startDate > :date ORDER BY b.startDate ASC")
	List<Booking> findByItemIdAndStartDateAfterOrderByStartDateAsc(@Param("id") Set<Long> id, @Param("date") LocalDateTime date); //future asc by item
}
