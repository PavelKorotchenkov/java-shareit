package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "requests")
public class ItemRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "request_id")
	private Long id;

	private String description;

	private LocalDateTime created;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "request")
	//@JoinColumn(name = "request_id")
	private List<Item> items;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
