package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<User> getAll() {
		return userRepository.getAll();
	}

	@Override
	public User getById(Long id) {
		return checkUser(id);
	}

	@Override
	public User update(Long id, User user) {
		checkUser(id);
		user.setId(id);
		return userRepository.update(user);
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	private User checkUser(Long id) {
		Optional<User> optUser = userRepository.getById(id);
		if (optUser.isEmpty()) {
			throw new UserNotFoundException("Пользователя с таким id не существует: " + id);
		}
		return optUser.get();
	}
}
