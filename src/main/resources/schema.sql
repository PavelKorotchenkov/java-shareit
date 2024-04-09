drop table if exists items, users, bookings, comments;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    name varchar(100) NOT NULL,
    email varchar(320) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    name varchar(100) NOT NULL,
    description varchar(1000),
    is_available boolean,
    user_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id) ON delete CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    item_id BIGINT,
    booker_id BIGINT,
    status varchar(20),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    text varchar(1000),
    item_id BIGINT,
    author_id BIGINT,
    created timestamp without time zone,
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE CASCADE
);

