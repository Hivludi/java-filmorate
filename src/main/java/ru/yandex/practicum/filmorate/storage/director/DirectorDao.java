package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Director> getDirectorById(int directorId) {
        String sql = "select * from DIRECTORS where DIRECTOR_ID = ?";
        Optional<Director> director = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), directorId)
                .stream()
                .findAny();
        if (director.isEmpty())
            throw new ObjectNotFoundException(String.format("Режисер с идентификатором %s не найден", directorId));
        return director;
    }

    public List<Director> listDirector() {
        return jdbcTemplate.query("select * from DIRECTORS", (rs, rowNum) -> makeDirector(rs));
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
        String sql = "update DIRECTORS set " +
                "NAME = ? " +
                "where DIRECTOR_ID = ?";
        jdbcTemplate.update(
                sql,
                director.getName(),
                director.getId());

        return getDirectorById(director.getId());
    }

    public void delete(int directorId) {
        getDirectorById(directorId);
        String deleteDirectorByIdQuery = "delete from DIRECTORS where DIRECTOR_ID = ?";
        jdbcTemplate.update(deleteDirectorByIdQuery, directorId);
    }

    public List<Director> listDirectorByFilmId(long filmId) {
        String sql = "select * from DIRECTORS where DIRECTOR_ID in (select DIRECTOR_ID from FILM_DIRECTORS where FILM_ID = ?)";
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
