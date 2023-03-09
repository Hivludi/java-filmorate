package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Mpa> getMpaById(int mpaId) {
        return jdbcTemplate
                .query("SELECT * FROM mpa WHERE mpa_id = ?", (rs, rowNum) -> makeMpa(rs), mpaId)
                .stream()
                .findAny();
    }

    public List<Mpa> listMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa", (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("MPA_ID"))
                .name(rs.getString("NAME"))
                .build();
    }
}
