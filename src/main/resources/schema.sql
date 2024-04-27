create TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    name varchar(100) NOT NULL,
    email varchar(320) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    description varchar(1000),
    created timestamp WITHOUT TIME ZONE,
    requester_id BIGINT,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(user_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name varchar(100) NOT NULL,
    description varchar(1000),
    is_available boolean,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(user_id) ON delete CASCADE,
    CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(request_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    item_id BIGINT,
    booker_id BIGINT,
    status varchar(20),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(item_id) ON delete CASCADE,
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(user_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    text varchar(1000),
    item_id BIGINT,
    author_id BIGINT,
    created timestamp WITHOUT TIME ZONE,
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(item_id) ON delete CASCADE,
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(user_id) ON delete CASCADE
);
