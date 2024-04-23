package ru.practicum.shareit.item.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findByUserId(long userId);

	@Query("select new Item(it.id, it.name, it.description, it.available, it.user) " +
			"from Item as it " +
			"where (lower(it.name) like %?1% or lower(it.description) like %?1%) " +
			"and it.available is true")
	Page<Item> findByNameOrDescriptionContainingAndAvailableTrue(String text, Pageable pageable);
}
