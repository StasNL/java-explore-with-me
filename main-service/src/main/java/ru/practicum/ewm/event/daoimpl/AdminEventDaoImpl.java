package ru.practicum.ewm.event.daoimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dao.AdminEventDao;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminEventDaoImpl implements AdminEventDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<FullEventResponse> getAllEventsForAdmin(List<Long> users,
                                                        List<String> states,
                                                        List<Long> categories,
                                                        Boolean paid,
                                                        Boolean onlyAvailable,
                                                        LocalDateTime rangeStart,
                                                        LocalDateTime rangeEnd,
                                                        Integer from,
                                                        Integer size) {

        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("user_ids", users);
        parameters.addValue("categories", categories);
        parameters.addValue("paid", paid);
        parameters.addValue("states", states);
        parameters.addValue("rangeStart", rangeStart);
        parameters.addValue("rangeEnd", rangeEnd);
        parameters.addValue("from", from);
        parameters.addValue("size", size);

        String where = "WHERE ";

        if (users != null && !users.isEmpty() && users.get(0) != 0) {
            where = where + " initiator_id IN (:user_ids) ";
        }

        if (categories != null && !categories.isEmpty() && categories.get(0) != 0) {

            if (!"WHERE ".equals(where))
                where = where + " AND";
            where = where + " category_id IN (:categories) ";
        }

        if (paid != null) {
            if (!"WHERE ".equals(where))
                where = where + " AND ";
            where = where + "paid IS :paid ";
        }

        if (states != null && !states.isEmpty()) {
            if (!"WHERE ".equals(where))
                where = where + " AND";
            where = where + " state IN (:states) ";
        }

        if (rangeStart != null) {
            if (!"WHERE ".equals(where))
                where = where + " AND";
            where = where + " e.creation_date > :rangeStart ";
        }

        if (rangeEnd != null) {
            if (!"WHERE ".equals(where))
                where = where + " AND";
            where = where + " e.creation_date < :rangeEnd ";
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
                "               COUNT(r.event_id) AS confirmed_requests, " +
                "               views, " +
                "               user_name, " +
                "               email, " +
                "               category_name, " +
                "               description, " +
                "               participants_limit, " +
                "               state, " +
                "               e.creation_date, " +
                "               publication_date, " +
                "               loc_id, " +
                "               longitude, " +
                "               latitude, " +
                "               request_moderation" +
                "       FROM events AS e " +
                "           LEFT JOIN requests r " +
                "               ON r.event_id = e.ev_id " +
                "               AND r.status = 'CONFIRMED'" +
                "           LEFT JOIN users AS u ON e.initiator_id = u.user_id " +
                "                   LEFT JOIN categories AS c ON e.category_id = c.cat_id " +
                "           LEFT JOIN locations AS l ON l.loc_id = e.location_id " +
                where +
                "       GROUP BY ev_id, " +
                "participants_limit, " +
                "title, " +
                "paid, " +
                "initiator_id, " +
                "event_date, " +
                "category_id, " +
                "annotation, " +
                "views," +
                "user_name," +
                "email, " +
                "category_name, " +
                "description, " +
                "participants_limit, " +
                "state, " +
                "e.creation_date, " +
                "publication_date, " +
                "loc_id, " +
                "longitude, " +
                "latitude, " +
                "request_moderation ";

        if (onlyAvailable != null && onlyAvailable) {
            sql = sql + "HAVING (participants_limit - COUNT(r.req_id)) > 0 ";
        }

        sql = sql + "LIMIT :size " +
                "OFFSET :from";


        return namedJdbcTemplate.query(sql, parameters, this::mapRowToEvent);
    }

    private FullEventResponse mapRowToEvent(ResultSet rs, int rowNumber) throws SQLException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(ru.practicum.ewm.utils.Constants.TIME_FORMAT);

        User initiator = User.builder()
                .userId(rs.getLong("initiator_id"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
                .build();

        Category category = Category.builder()
                .catId(rs.getLong("category_id"))
                .name(rs.getString("category_name"))
                .build();

        Location location = Location.builder()
                .lat(rs.getFloat("latitude"))
                .lon(rs.getFloat("longitude"))
                .locId(rs.getLong("loc_id"))
                .build();

        return FullEventResponse.builder()
                .id(rs.getLong("ev_id"))
                .title(rs.getString("title"))
                .views(rs.getLong("views"))
                .paid(rs.getBoolean("paid"))
                .confirmedRequests(rs.getLong("confirmed_requests"))
                .initiator(initiator)
                .eventDate(rs.getTimestamp("event_date").toLocalDateTime().format(dtf))
                .category(category)
                .annotation(rs.getString("annotation"))
                .description(rs.getString("description"))
                .participantLimit(rs.getLong("participants_limit"))
                .state(rs.getString("state"))
                .createdOn(rs.getTimestamp("creation_date").toLocalDateTime().format(dtf))
                .publishedOn(rs.getTimestamp("publication_date").toLocalDateTime().format(dtf))
                .location(location)
                .requestModeration(rs.getBoolean("request_moderation"))
                .build();
    }
}
