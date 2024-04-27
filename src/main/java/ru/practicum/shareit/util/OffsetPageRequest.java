package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest extends PageRequest {
	protected OffsetPageRequest(int page, int size, Sort sort) {
		super(page, size, sort);
	}

	public static PageRequest createPageRequest(Integer from, Integer size) {
		PageRequest page;
		if (from == null || size == null) {
			page = PageRequest.of(0, Integer.MAX_VALUE);
		} else if (from < 0) {
			throw new IllegalArgumentException("Параметр 'from' должен быть больше нуля.");
		} else {
			page = PageRequest.of(from / size, size);
		}
		return page;
	}
}
