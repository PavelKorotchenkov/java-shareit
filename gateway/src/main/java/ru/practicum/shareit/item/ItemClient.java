package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
	private static final String API_PREFIX = "/items";


	@Autowired
	public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(HttpComponentsClientHttpRequestFactory::new)
						.build()
		);
	}

	public ResponseEntity<Object> addItem(long ownerId, ItemCreateDto itemCreateDto) {
		return post("", ownerId, itemCreateDto);
	}


	public ResponseEntity<Object> updateItem(long id, long ownerId, ItemUpdateDto itemUpdateDto) {
		return patch("/" + id, ownerId, itemUpdateDto);
	}


	public ResponseEntity<Object> getItem(long userId, Long id) {
		return get("/" + id, userId);
	}

	public ResponseEntity<Object> findAllByOwnerId(long ownerId, int from, int size) {
		Map<String, Object> parameters = Map.of(
				"from", from,
				"size", size
		);
		return get("?from={from}&size={size}", ownerId, parameters);
	}

	public ResponseEntity<Object> searchBy(long userId, String text, int from, int size) {
		Map<String, Object> parameters = Map.of(
				"text", text,
				"from", from,
				"size", size
		);
		return get("/search?text={text}&from={from}&size={size}", userId, parameters);
	}


	public ResponseEntity<Object> addComment(long userId, Long itemId, CommentRequestDto commentRequestDto) {
		String path = "/" + itemId + "/comment";
		return post(path, userId, commentRequestDto);
	}
}
