package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
	Optional<ItemRequest> findById(long id);

	List<ItemRequest> findByRequesterId(long userId, Sort sort);

	Page<ItemRequest> findByRequesterIdNot(long userId, Pageable pageable);

}
