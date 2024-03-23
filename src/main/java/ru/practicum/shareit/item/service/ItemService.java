package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {
	Item add(Item item, User owner);

	Item update(Item item, Long itemId, User owner);

	Item getById(Long id);

	List<Item> showItemsByOwner(Long id);

	List<Item> searchAvailableByText(String text);
}
