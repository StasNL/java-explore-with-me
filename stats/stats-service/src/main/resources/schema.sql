DROP TABLE IF EXISTS hits;

CREATE TABLE IF NOT EXISTS hits
(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
app VARCHAR(32) NOT NULL,
uri VARCHAR(100) NOT NULL,
ip VARCHAR(32) NOT NULL,
created timestamp NOT NULL
);