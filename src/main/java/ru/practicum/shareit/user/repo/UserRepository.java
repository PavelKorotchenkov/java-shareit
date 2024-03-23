package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
	User save(User user);

	List<User> getAll();

	Optional<User> getById(Long id);

	User update(User user);

	void deleteById(Long id);
}
