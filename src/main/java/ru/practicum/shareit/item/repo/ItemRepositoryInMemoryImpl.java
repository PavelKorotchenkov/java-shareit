package ru.practicum.shareit.item.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {
	private final Map<Long, Item> items = new HashMap<>();
	private Long id = 1L;

	@Override
	public Item add(Item item) {
		item.setId(id++);
		items.put(item.getId(), item);
		return items.get(item.getId());
	}

	@Override
	public Item update(Item item) {
		Item itemToUpdate = items.get(item.getId());
		if (item.getName() != null) {
			itemToUpdate.setName(item.getName());
		}
		if (item.getDescription() != null) {
			itemToUpdate.setDescription(item.getDescription());
		}
		if (item.getAvailable() != null) {
			itemToUpdate.setAvailable(item.getAvailable());
		}
		return itemToUpdate;
	}

	@Override
	public Optional<Item> getById(Long id) {
		Item item = items.get(id);
		return Optional.ofNullable(item);
	}

	@Override
	public List<Item> showItemsByOwner(Long id) {
		return items.values()
				.stream()
				.filter(item -> item.getOwner().getId().equals(id))
				.collect(Collectors.toList());
	}

	@Override
	public List<Item> searchAvailableByText(String text) {
		return items.values()
				.stream()
				.filter(item -> item.getAvailable() &&
						(item.getName().toLowerCase().contains(text) ||
								item.getDescription().toLowerCase().contains(text)))
				.collect(Collectors.toList());
	}
}
