package ru.practicum.shareit.user.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
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
			throw new AlreadyExistsException("Пользователь с такой эл. почтой уже существует: " + user);
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
	public User update(User userUpdate) {
		long id = userUpdate.getId();
		User currentUser = users.get(id);
		if (userEmails.contains(userUpdate.getEmail()) && !userUpdate.getEmail().equals(currentUser.getEmail())) {
			throw new AlreadyExistsException("Пользователь с такой эл.почтой уже существует: " + userUpdate);
		}
		userEmails.remove(currentUser.getEmail());
		userEmails.add(userUpdate.getEmail());
		users.put(id, userUpdate);

		return users.get(id);
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
