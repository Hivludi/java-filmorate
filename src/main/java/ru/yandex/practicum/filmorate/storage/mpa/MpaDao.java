package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Mpa> getMpaById(int mpaId) {
        return jdbcTemplate
                .query("select * from MPA where MPA_ID = ?", (rs, rowNum) -> makeMpa(rs), mpaId)
                .stream()
                .findAny();
    }

    public List<Mpa> listMpa() {
        return jdbcTemplate.query("select * from MPA", (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("MPA_ID"))
                .name(rs.getString("NAME"))
                .build();
    }
}
