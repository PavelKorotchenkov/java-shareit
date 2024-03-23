package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
	Item add(Item item);

	Item update(Item item);

	Optional<Item> getById(Long id);

	List<Item> showItemsByOwner(Long id);

	List<Item> searchAvailableByText(String text);
}
