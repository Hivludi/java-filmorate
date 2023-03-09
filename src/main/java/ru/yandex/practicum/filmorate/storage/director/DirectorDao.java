package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Director> getDirectorById(int directorId) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        Optional<Director> director = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), directorId)
                .stream()
                .findAny();
        if (director.isEmpty())
            throw new ObjectNotFoundException(String.format("Режисер с идентификатором %s не найден", directorId));
        return director;
    }

    public List<Director> listDirector() {
        return jdbcTemplate.query("SELECT * FROM directors", (rs, rowNum) -> makeDirector(rs));
    }

    public Optional<Director> create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID");
        int directorId = simpleJdbcInsert.executeAndReturnKey(toMap(director)).intValue();

        return getDirectorById(directorId);
    }

    public Optional<Director> update(Director director) {
        getDirectorById(director.getId());
        String sql = "UPDATE directors SET " +
                "name = ? " +
                "WHERE director_id = ?";
        jdbcTemplate.update(
                sql,
                director.getName(),
                director.getId());

        return getDirectorById(director.getId());
    }

    public void delete(int directorId) {
        getDirectorById(directorId);
        String deleteDirectorByIdQuery = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(deleteDirectorByIdQuery, directorId);
    }

    public List<Director> listDirectorByFilmId(long filmId) {
        String sql = "SELECT * FROM directors WHERE director_id in " +
                "(SELECT director_id FROM film_directors WHERE film_id = ?)";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId));
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("DIRECTOR_ID"))
                .name(rs.getString("NAME"))
                .build();
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", director.getName());
        return values;
    }
}
