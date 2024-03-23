package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;

	@Autowired
	public ItemServiceImpl(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Override
	public Item add(Item item, User owner) {
		item.setOwner(owner);
		return itemRepository.add(item);
	}

	@Override
	public Item update(Item item, Long itemId, User owner) {
		Item checkedItem = getById(itemId);
		if (!Objects.equals(owner.getId(), checkedItem.getOwner().getId())) {
			throw new AccessDeniedException("Доступ к редактированию запрещен, " +
					"только владелец может редактировать вещь.");
		}
		item.setId(itemId);
		item.setOwner(owner);
		return itemRepository.update(item);
	}

	@Override
	public Item getById(Long id) {
		return checkItem(id);
	}

	@Override
	public List<Item> showItemsByOwner(Long id) {
		return itemRepository.showItemsByOwner(id);
	}

	@Override
	public List<Item> searchAvailableByText(String text) {
		if (text.isEmpty() || text.isBlank()) {
			return new ArrayList<>();
		}
		return itemRepository.searchAvailableByText(text);
	}

	private Item checkItem(Long id) {
		Optional<Item> optItem = itemRepository.getById(id);
		if (optItem.isEmpty()) {
			throw new ItemNotFoundException("Вещь с таким id не найдена: " + id);
		}
		return optItem.get();
	}
}
