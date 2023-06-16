package ru.practicum.ewm.event.daoimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dao.AdminEventDao;
import ru.practicum.ewm.event.model.ShortEvent;
import ru.practicum.ewm.event.model.enums.State;
import ru.practicum.ewm.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminEventDaoImpl implements AdminEventDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ShortEvent> getAllEventsForAdmin(List<Long> users,
                                                 List<State> states,
                                                 List<Category> categories,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Integer from,
                                                 Integer size) {

        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("user_ids", users);
        parameters.addValue("categories", categories);
        parameters.addValue("states", states);
        parameters.addValue("rangeStart", rangeStart);
        parameters.addValue("rangeEnd", rangeEnd);
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
                "               COUNT(r.event_id) AS confirmed_requests, " +
                "               views " +
                "               u.user_name, " +
                "               u.email, " +
                "               c.category_name "  +
                "       FROM events AS e " +
                "           LEFT JOIN requests r " +
                "               ON r.event_id = e.ev_id " +
                "               AND r.status = 'CONFIRMED'" +
                "           LEFT JOIN users AS u ON e.initiator_id = u.user_id " +
        "                   LEFT JOIN categories AS c ON e.category_id = c.cat_id " +
                "       WHERE 1=1 " +
                "       GROUP BY ev_id, event_date, views ";

        if (users != null && !users.isEmpty()) {
            sql = sql + "AND initiator_id IN (:user_ids)";
        }
        if (categories != null && !categories.isEmpty()) {
            sql = sql + "AND category_id IN (:categories) ";
        }
        if (states != null) {
            sql = sql + "AND state IN (:states) ";
        }
        if (rangeStart != null) {
            sql = sql + "AND e.creation_date >= :rangeStart ";
        }
        if (rangeEnd != null) {
            sql = sql + "AND e.creation_date <= :rangeEnd ";
        }
        sql = sql + "LIMIT :size " +
                "OFFSET :from";


        return namedJdbcTemplate.query(sql, parameters, this::mapRowToEvent);
    }

    private ShortEvent mapRowToEvent(ResultSet rs, int rowNumber) throws SQLException {

        User initiator = User.builder()
                .userId(rs.getLong("initiator_id"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
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
                .build();
    }
}
