package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findByUserId(long userId);

	@Query("select new Item(it.id, it.name, it.description, it.isAvailable, it.user) " +
			"from Item as it " +
			"where (lower(it.name) like %?1% or lower(it.description) like %?1%) " +
			"and it.isAvailable is true")
	List<Item> findByNameOrDescriptionContainingAndAvailableTrue(String text);
}
