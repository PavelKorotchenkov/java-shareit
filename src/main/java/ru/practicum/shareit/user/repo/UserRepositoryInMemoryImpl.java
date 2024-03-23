package ru.practicum.shareit.user.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
	private Long userId = 1L;
	private final Map<Long, User> users = new HashMap<>();
	private final Set<String> userEmails = new HashSet<>();

	@Override
	public User save(User user) {
		String email = user.getEmail().toLowerCase();
		if (userEmails.contains(email)) {
			throw new UserAlreadyExistsException("Пользователь с такой эл. почтой уже существует: " + user);
		}
		user.setId(userId++);
		long userId = user.getId();
		users.put(userId, user);
		userEmails.add(email);
		return users.get(userId);
	}

	@Override
	public List<User> getAll() {
		return new ArrayList<>(users.values());
	}

	@Override
	public Optional<User> getById(Long id) {
		User user = users.get(id);
		return Optional.ofNullable(user);
	}

	@Override
	public User update(User user) {
		long id = user.getId();
		User currentUser = users.get(id);
		if (user.getName() == null) {
			user.setName(currentUser.getName());
		}

		if (user.getEmail() == null) {
			user.setEmail(currentUser.getEmail());
		} else {
			if (userEmails.contains(user.getEmail()) && !user.getEmail().equals(currentUser.getEmail())) {
				throw new UserAlreadyExistsException("Пользователь с такой эл.почтой уже существует: " + user);
			}
			userEmails.remove(currentUser.getEmail());
			userEmails.add(user.getEmail());
		}
		users.put(id, user);

		return user;
	}

	@Override
	public void deleteById(Long id) {
		Optional<User> optUser = getById(id);
		if (optUser.isPresent()) {
			User user = optUser.get();
			userEmails.remove(user.getEmail());
		}
		users.remove(id);
	}
}
