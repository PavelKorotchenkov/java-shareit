package ru.practicum.shareit.item.model;

import lombok.*;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private long id;

	@NotEmpty
	private String name;

	private String description;

	@Column(name = "is_available")
	private Boolean available;

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	@JoinColumn(name = "request_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private ItemRequest request;

	public Item(long id, String name, String description, Boolean isAvailable, User user) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.available = isAvailable;
		this.user = user;
	}
}
