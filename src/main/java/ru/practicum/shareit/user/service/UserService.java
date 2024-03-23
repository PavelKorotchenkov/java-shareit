package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
	User save(User user);

	List<User> getAll();

	User getById(Long id);

	User update(Long id, User user);

	void deleteById(Long id);
}
