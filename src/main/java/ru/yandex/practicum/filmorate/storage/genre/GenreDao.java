package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Genre> getGenreById(int genreId) {
        return jdbcTemplate
                .query("select * from GENRES where GENRE_ID = ?", (rs, rowNum) -> makeGenre(rs), genreId)
                .stream()
                .findAny();
    }

    public List<Genre> listGenre() {
        return jdbcTemplate.query("select * from GENRES", (rs, rowNum) -> makeGenre(rs));
    }

    public Set<Genre> listGenreByFilmId(long filmId) {
        String sql = "select * from GENRES where GENRE_ID in (select GENRE_ID from FILM_GENRES where FILM_ID = ?)";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("NAME"))
                .build();
    }
}
