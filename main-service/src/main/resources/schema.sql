CREATE TABLE IF NOT EXISTS users
(
    user_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    user_name VARCHAR(32)                             NOT NULL,
    email     VARCHAR(32)                             NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations
(
    comp_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    pinned  BOOLEAN                                 NOT NULL,
    title   VARCHAR                                 NOT NULL
);

CREATE TABLE IF NOT EXISTS locations
(
    loc_id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    latitude  FLOAT                                   NOT NULL,
    longitude FLOAT                                   NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    cat_id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    category_name VARCHAR(20)                             NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    ev_id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    title              VARCHAR(64)                             NOT NULL,
    description        VARCHAR(1000)                           NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    creation_date      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    publication_date   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    participants_limit BIGINT                                  NOT NULL,
    request_moderation BOOLEAN                                 NOT NULL,
    annotation         VARCHAR(200)                            NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    location_id        BIGINT                                  NOT NULL,
    state              VARCHAR(20)                             NOT NULL,
    views              BIGINT                                  NOT NULL,
    CONSTRAINT fk_events_to_users FOREIGN KEY (initiator_id) REFERENCES users (user_id),
    CONSTRAINT fk_events_to_locations FOREIGN KEY (location_id) REFERENCES locations (loc_id),
    CONSTRAINT fk_events_to_categories FOREIGN KEY (category_id) REFERENCES categories (cat_id)
);

CREATE TABLE IF NOT EXISTS requests
(
    req_id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    creation_date TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_id      BIGINT                                  NOT NULL,
    requester_id  BIGINT                                  NOT NULL,
    status        VARCHAR                                 NOT NULL,

    CONSTRAINT fk_req_to_users FOREIGN KEY (requester_id) REFERENCES users (user_id),
    CONSTRAINT fk_req_to_events FOREIGN KEY (event_id) REFERENCES events (ev_id)
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id BIGINT REFERENCES compilations (comp_id) ON DELETE CASCADE,
    event_id       BIGINT REFERENCES events (ev_id) ON DELETE CASCADE
);