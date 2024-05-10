package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class UserDtoMapperTest {
	@Autowired
	private JacksonTester<UserDto> jsonDto;
	@Autowired
	private JacksonTester<User> jsonUser;

	@Test
	void toDto() throws IOException {

		UserDto userDto = UserDto.builder().id(1L).email("mail@mail.ru").name("name").build();

		JsonContent<UserDto> result = jsonDto.write(userDto);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mail@mail.ru");
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
	}

	@Test
	void toUser() throws IOException {
		User user = User.builder().id(1L).email("mail@mail.ru").name("name").build();
		JsonContent<User> result = jsonUser.write(user);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mail@mail.ru");
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
	}
}