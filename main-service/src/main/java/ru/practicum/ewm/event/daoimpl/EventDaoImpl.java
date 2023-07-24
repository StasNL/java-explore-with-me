package ru.practicum.ewm.event.daoimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.model.ShortEvent;
import ru.practicum.ewm.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public List<ShortEvent> getFilteredEvents(String text,
                                              List<Long> categories,
                                              Boolean paid,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Integer from,
                                              Integer size,
                                              String sortBy,
                                              Boolean onlyAvailable,
                                              Integer minRating) {

        namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("text", text);
        parameters.addValue("categories", categories);
        parameters.addValue("paid", paid);
        parameters.addValue("rangeStart", rangeStart);
        parameters.addValue("rangeEnd", rangeEnd);
        parameters.addValue("from", from);
        parameters.addValue("size", size);
        parameters.addValue("sortBy", sortBy);
        parameters.addValue("min_rating", minRating);

        String where = "WHERE ";
        if (text != null) {
            where = where + "(annotation ILIKE '%'||:text||'%' OR description ILIKE '%'||:text||'%') ";
        }
        if (categories != null && !categories.isEmpty()) {
            if (!"WHERE ".equals(where))
                where = where + "AND ";
            where = where + "category_id IN (:categories) ";
        }
        if (paid != null) {
            if (!"WHERE ".equals(where))
                where = where + "AND ";
            where = where + "paid = :paid ";
        }
        if (rangeStart != null) {
            if (!"WHERE ".equals(where))
                where = where + "AND ";
            where = where + "e.creation_date > :rangeStart ";
        }
        if (rangeEnd != null) {
            if (!"WHERE ".equals(where))
                where = where + "AND ";
            where = where + "e.creation_date < :rangeEnd ";
        }

        if (minRating != null) {
            if (!"WHERE ".equals(where))
                where = where + "AND ";
            where = where + "e.rating >= :min_rating ";
        }

        if ("WHERE ".equals(where))
            where = "";

        String sql = "SELECT " +
                "               ev_id, " +
                "               title, " +
                "               paid, " +
                "               initiator_id, " +
                "               event_date, " +
                "               category_id, " +
                "               annotation, " +
                "               COUNT(r.req_id) AS confirmed_requests, " +
                "               views, " +
                "               user_name, " +
                "               email, " +
                "               category_name," +
                "               e.creation_date," +
                "               e.rating " +
                "       FROM events AS e " +
        "                           LEFT JOIN requests r " +
                "                               ON r.event_id = e.ev_id " +
                "                               AND r.status = 'CONFIRMED' " +
                "                           LEFT JOIN users AS u ON e.initiator_id = u.user_id " +
                "                           LEFT JOIN categories AS c ON e.category_id = c.cat_id " +
                        where +
                "       GROUP BY ev_id, " +
                "               participants_limit, " +
                "               title," +
                "               paid, " +
                "               initiator_id, " +
                "               event_date, " +
                "               category_id, " +
                "               annotation, " +
                "               views," +
                "               user_name, " +
                "               email, " +
                "               category_name," +
                "               e.creation_date," +
                "               e.rating ";

        if (onlyAvailable != null && onlyAvailable) {
            sql = sql + "HAVING (participants_limit - COUNT(r.req_id)) > 0 ";
        }

        if ("EVENT_DATE".equals(sortBy)) {
            sql = sql + "ORDER BY event_date ";
        } else if ("VIEWS".equals(sortBy)) {
            sql = sql + "ORDER BY views ";
        }
        sql = sql + "LIMIT :size " +
                "OFFSET :from";

        return namedJdbcTemplate.query(sql, parameters, this::mapRowToEvent);
    }

    @Override
    public List<ShortEvent> getAllEventsByUserId(Long initiatorId, Integer from, Integer size) {
        namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("initiator_id", initiatorId);
        parameters.addValue("from", from);
        parameters.addValue("size", size);

        String sql = "SELECT " +
                "               ev_id, " +
                "               title, " +
                "               paid, " +
                "               initiator_id, " +
                "               event_date, " +
                "               category_id, " +
                "               annotation, " +
                "               COUNT(r.req_id) AS confirmed_requests, " +
                "               views, " +
                "               user_name, " +
                "               email, " +
                "               category_name," +
                "               e.creation_date," +
                "               e.rating " +
                "       FROM events AS e " +
                "                           LEFT JOIN requests r " +
                "                               ON r.event_id = e.ev_id " +
                "                               AND r.status = 'CONFIRMED'" +
                "                           LEFT JOIN users AS u ON e.initiator_id = u.user_id " +
                "                           LEFT JOIN categories AS c ON e.category_id = c.cat_id " +
                "       WHERE initiator_id = :initiator_id  " +
                "       GROUP BY ev_id, " +
                "               title, " +
                "               paid, " +
                "               initiator_id, " +
                "               event_date, " +
                "               category_id, " +
                "               annotation, " +
                "               views, " +
                "               user_name, " +
                "               email, " +
                "               category_name," +
                "               e.creation_date," +
                "               e.rating " +
                "       LIMIT :size " +
                "       OFFSET :from ";

        return namedJdbcTemplate.query(sql, parameters, this::mapRowToEvent);
    }

    @Override
    public List<ShortEvent> getAllEventsByCompilationId(Long compId) {
        namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("comp_id", compId);

        String sql = "SELECT " +
                "               ev_id, " +
                "               e.title, " +
                "               paid, " +
                "               initiator_id, " +
                "               event_date, " +
                "               category_id, " +
                "               annotation, " +
                "               COUNT(r.req_id) AS confirmed_requests, " +
                "               views, " +
                "               user_name, " +
                "               email, " +
                "               category_name," +
                "               e.creation_date," +
                "               e.rating " +
                "       FROM events AS e " +
                "           LEFT JOIN requests r " +
                "               ON r.event_id = e.ev_id " +
                "               AND r.status = 'CONFIRMED'" +
                "           LEFT JOIN users AS u ON e.initiator_id = u.user_id " +
                "           LEFT JOIN categories AS cat ON e.category_id = cat.cat_id " +
                "           LEFT JOIN compilations_events AS ce ON e.ev_id = ce.event_id " +
                "           JOIN compilations AS comp on comp.comp_id = ce.compilation_id " +
                "       WHERE comp_id = :comp_id " +
                "       GROUP BY ev_id, " +
                "               e.title, " +
                "               paid, " +
                "               initiator_id, " +
                "               event_date, " +
                "               category_id, " +
                "               annotation, " +
                "               views, " +
                "               user_name, " +
                "               email, " +
                "               category_name," +
                "               e.creation_date," +
                "               e.rating ";

        return namedJdbcTemplate.query(sql, parameters, this::mapRowToEvent);
    }

    @Override
    public void updateViews(Map<Long, Long> views) {
        for (Map.Entry<Long, Long> entry : views.entrySet()) {

            String sql = "  UPDATE events " +
                    "       SET views = " + entry.getValue() +
                    "       WHERE ev_id = " + entry.getKey();
            jdbcTemplate.update(sql);
        }
    }

    @Override
    public void addRating(Long userId, Long eventId, Integer rating) {
        namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("user_id", userId);
        parameters.addValue("event_id", eventId);
        parameters.addValue("rating", rating);

        String sql = "INSERT INTO likes (event_id, user_id, rate) " +
                "       VALUES (:event_id, :user_id, :rating) ";

        namedJdbcTemplate.update(sql, parameters);
    }

    public void countRating(Long userId, Long eventId) {
        namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("user_id", userId);
        parameters.addValue("event_id", eventId);

        String sql = "UPDATE events " +
                "       SET rating = (SELECT ROUND(AVG(l.rate), 1) " +
                "                       FROM likes AS l" +
                "                       WHERE l.event_id = :event_id )" +
                "       WHERE ev_id = :event_id;" +
                "       UPDATE users " +
                "       SET rating = (SELECT ROUND(AVG(l.rate), 1) " +
                "                       FROM likes AS l " +
                "                       WHERE l.user_id = :user_id) " +
                "       WHERE user_id = :user_id ";

        namedJdbcTemplate.update(sql, parameters);
    }

    @Override
    public Long getConfirmedRequestsByEventId(Long eventId) {
        String sql = "SELECT e.participants_limit " +
                "FROM events AS e " +
                "WHERE ev_id = ?";

        return jdbcTemplate.queryForObject(sql, Long.class, eventId);
    }

    @Override
    public Map<Long, List<ShortEvent>> getAllEventsByCompilations(List<Long> compIds) {
        Map<Long, List<ShortEvent>> shortEventMap = new LinkedHashMap<>();
        for (Long compId : compIds) {
            List<ShortEvent> shortEvents = getAllEventsByCompilationId(compId);
            shortEventMap.put(compId, shortEvents);
        }
        return shortEventMap;
    }

    private ShortEvent mapRowToEvent(ResultSet rs, int rowNumber) throws SQLException {

        User initiator = User.builder()
                .userId(rs.getLong("initiator_id"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
                .rating(rs.getFloat("rating"))
                .build();

        Category category = Category.builder()
                .catId(rs.getLong("category_id"))
                .name(rs.getString("category_name"))
                .build();

        return ShortEvent.builder()
                .id(rs.getLong("ev_id"))
                .title(rs.getString("title"))
                .views(rs.getLong("views"))
                .paid(rs.getBoolean("paid"))
                .confirmedRequests(rs.getLong("confirmed_requests"))
                .initiator(initiator)
                .eventDate(rs.getTimestamp("event_date").toLocalDateTime())
                .category(category)
                .annotation(rs.getString("annotation"))
                .publicationDate(rs.getTimestamp("creation_date").toLocalDateTime())
                .rating(rs.getFloat("rating"))
                .build();
    }
}