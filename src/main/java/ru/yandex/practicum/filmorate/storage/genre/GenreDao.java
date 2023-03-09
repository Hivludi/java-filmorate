package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Genre> getGenreById(int genreId) {
        return jdbcTemplate
                .query("SELECT * FROM genres WHERE genre_id = ?", (rs, rowNum) -> makeGenre(rs), genreId)
                .stream()
                .findAny();
    }

    public List<Genre> listGenre() {
        return jdbcTemplate.query("SELECT * FROM genres", (rs, rowNum) -> makeGenre(rs));
    }

    public Set<Genre> listGenreByFilmId(long filmId) {
        String sql = "SELECT * FROM genres WHERE genre_id IN (SELECT genre_id FROM film_genres WHERE film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("NAME"))
                .build();
    }
}
