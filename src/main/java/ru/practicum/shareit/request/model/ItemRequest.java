package ru.practicum.shareit.request.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemRequest {
	private Long id;
	private String description;
	private Long requestor;
	private LocalDateTime created;
}
