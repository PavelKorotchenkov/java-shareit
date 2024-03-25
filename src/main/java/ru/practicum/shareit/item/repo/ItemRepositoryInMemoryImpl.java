package ru.practicum.shareit.item.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {
	private final Map<Long, Item> items = new HashMap<>();
	private final Map<Long, List<Long>> itemsByOwnerId = new HashMap<>();
	private Long id = 1L;

	@Override
	public Item add(Item item) {
		item.setId(id++);
		items.put(item.getId(), item);

		long ownerId = item.getOwner().getId();
		List<Long> ownerItems = itemsByOwnerId.getOrDefault(ownerId, new ArrayList<>());
		ownerItems.add(item.getId());
		itemsByOwnerId.put(ownerId, ownerItems);

		return items.get(item.getId());
	}

	@Override
	public Item update(Item item) {
		long itemId = item.getId();
		items.put(itemId, item);
		return items.get(itemId);
	}

	@Override
	public Optional<Item> getById(Long id) {
		Item item = items.get(id);
		return Optional.ofNullable(item);
	}

	@Override
	public List<Long> findByOwnerId(Long id) {
		return itemsByOwnerId.get(id);
	}

	@Override
	public List<Item> searchBy(String text) {
		return items.values()
				.stream()
				.filter(item -> item.getAvailable() &&
						(item.getName().toLowerCase().contains(text) ||
								item.getDescription().toLowerCase().contains(text)))
				.collect(Collectors.toList());
	}
}
