package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "comments")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private long id;
	private String text;
	@JoinColumn(name = "item_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private Item item;
	@JoinColumn(name = "author_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private User author;
	private LocalDateTime created;
}
