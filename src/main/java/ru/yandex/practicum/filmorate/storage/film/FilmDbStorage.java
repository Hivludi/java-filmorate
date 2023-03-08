package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("FilmDB")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final DirectorDao directorDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDao genreDao, MpaDao mpaDao, DirectorDao directorDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.directorDao = directorDao;
    }

    @Override
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        int filmId = simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue();
        if (film.getMpa() != null) {
            String sql = "update FILMS set MPA_ID = ? where FILM_ID = ?";
            jdbcTemplate.update(sql, film.getMpa().getId(), filmId);
        }
        addFilmParameters(film, filmId);

        return findFilmById(filmId);
    }

    @Override
    public Optional<Film> update(Film film) {
        findFilmById(film.getId());
        String sql = "update FILMS set " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ? " +
                "where FILM_ID = ?";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        String mpaUpdateSql = "update FILMS set MPA_ID = ? where FILM_ID = ?";
        if (film.getMpa() != null) {
            jdbcTemplate.update(mpaUpdateSql, film.getMpa().getId(), film.getId());
        } else {
            jdbcTemplate.update(mpaUpdateSql, null, film.getId());
        }

        int filmId = film.getId();

        jdbcTemplate.update("delete from FILM_GENRES where FILM_ID = ?", filmId);
        jdbcTemplate.update("delete from FILM_LIKES where FILM_ID = ?", filmId);
        jdbcTemplate.update("delete from FILM_DIRECTORS where FILM_ID = ?", filmId);

        addFilmParameters(film, filmId);

        return findFilmById(film.getId());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sql = "select * from FILMS where FILM_ID = ?";
        Optional<Film> film = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id).stream()
                .findAny();
        if (film.isEmpty())
            throw new ObjectNotFoundException(String.format("Фильм с идентификатором %s не найден", id));
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "select * from FILMS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> addLike(int userId, int filmId) {
        findFilmById(filmId);
        if (getLikes(filmId).contains(userId))
            return findFilmById(filmId);
        String insertLikeQuery = "insert into FILM_LIKES (FILM_ID, USER_ID) VALUES (?,?)";
        jdbcTemplate.update(insertLikeQuery, filmId, userId);
        return findFilmById(filmId);
    }

    @Override
    public Optional<Film> removeLike(int userId, int filmId) {
        findFilmById(filmId);
        if (!getLikes(filmId).contains(userId))
            throw new LikeNotFoundException("Фильм не содержит лайк от данного пользователя");
        String deleteLikeQuery = "delete from FILM_LIKES where USER_ID = ? and FILM_ID = ?";
        jdbcTemplate.update(deleteLikeQuery, userId, filmId);
        return findFilmById(filmId);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        String showMostPopularFilmsQuery = "select f.FILM_ID from FILMS f " +
                "left join FILM_GENRES fg on f.FILM_ID = fg.FILM_ID " +
                "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                "where (? is null or fg.GENRE_ID = ?) " +
                "and (? is null or extract(year from f.RELEASE_DATE) = ?) " +
                "group by f.FILM_ID " +
                "order by count(fl.USER_ID) desc " +
                "limit ?";
        return jdbcTemplate.queryForList(showMostPopularFilmsQuery, Integer.class,
                        genreId.orElse(null),
                        genreId.orElse(null),
                        year.orElse(null),
                        year.orElse(null),
                        count)
                .stream()
                .map(this::findFilmById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> showCommonFilms(int userId, int friendId) {
        String showCommonFilmsQuery = "select f.FILM_ID from FILMS f " +
                "join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                "where f.FILM_ID in " +
                "(select FILM_ID from FILM_LIKES fl2 " +
                "where USER_ID in (?,?) " +
                "group by FILM_ID " +
                "having count(USER_ID) = 2) " +
                "group by f.FILM_ID " +
                "order by count(fl.USER_ID) desc";
        return jdbcTemplate.queryForList(showCommonFilmsQuery, Integer.class, userId, friendId)
                .stream()
                .map(this::findFilmById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilmById(Integer filmId) {
        findFilmById(filmId);
        String deleteFilmByIdQuery = "delete from FILMS where FILM_ID=?";
        jdbcTemplate.update(deleteFilmByIdQuery, filmId);
    }

    public Collection<Film> showFilmsUserLikes(Integer userId) {
        String filmsUserLikesSQL =
                "SELECT f.* FROM FILMS f LEFT JOIN FILM_LIKES fl ON (f.FILM_ID = fl.FILM_ID) WHERE fl.USER_ID = ?";

        return jdbcTemplate.query(filmsUserLikesSQL,
                (rs, rowNum) -> makeFilm(rs),
                userId);
    }

    @Override
    public Collection<Film> showRecommendations(Integer userId) {
        String filmPreferencesOtherUsersSQL =
                "SELECT f.* FROM FILMS f LEFT JOIN FILM_LIKES fl ON (f.FILM_ID = fl.FILM_ID) WHERE fl.USER_ID = ?"
                        + " EXCEPT "
                        + " SELECT f2.* FROM FILMS f2 "
                        + "LEFT JOIN FILM_LIKES fl ON (f2.FILM_ID = fl.FILM_ID) WHERE fl.USER_ID = ?";

        // Все пользователи, которым также нравится фильмы пользователя, которому нужна рекомендация
        Set<Integer> otherUserIds = showFilmsUserLikes(userId).stream()
                .collect(HashSet::new,
                        (collection, film) ->
                                collection.addAll(film.getLikes()),
                        Collection::addAll);

        otherUserIds.remove(userId);

        // Все понравившиеся фильмы, всех пользователей за исключением фильмов пользователя, которому нужна рекомендация
        return otherUserIds.stream()
                .collect(HashSet::new,
                        (collection, otherUserId) ->
                                collection.addAll(jdbcTemplate.query(filmPreferencesOtherUsersSQL,
                                        (rs, rowNum) -> makeFilm(rs),
                                        otherUserId,
                                        userId)),
                        Collection::addAll);
    }

    @Override
    public List<Film> findDirectorFilms(int directorId, String sortBy) {
        directorDao.getDirectorById(directorId);
        if (sortBy.equals("year")) {
            String sql = "select * from FILMS AS f where f.FILM_ID in (" +
                        "select fd.FILM_ID from FILM_DIRECTORS AS fd where fd.DIRECTOR_ID = ?" +
                    ") order by f.RELEASE_DATE asc";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), directorId);
        } else {
            String sql = "select * from FILMS AS f where f.FILM_ID in (" +
                        "select fd1.FILM_ID from FILM_DIRECTORS AS fd1 where fd1.DIRECTOR_ID = ?" +
                    ") order by (" +
                        "select count(fl.USER_ID) from FILM_LIKES AS fl where fl.FILM_ID in (" +
                            "select fd2.FILM_ID from FILM_DIRECTORS AS fd2 where fd2.DIRECTOR_ID = ?" +
                        ") group by fl.FILM_ID" +
                    ") desc";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), directorId, directorId);
        }
    }

    @Override
    public List<Film> searchFilmsByNameOrDirector(String query) {
        String sqlQuery = "select distinct f.*, " +
                "M.NAME, " +
                "count(fl.FILM_ID)" +
                "from FILMS f " +
                "left join MPA m on f.MPA_ID = m.MPA_ID " +
                "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                "left join FILM_DIRECTORS fd on f.FILM_ID=fd.FILM_ID " +
                "left join DIRECTORS d on fd.DIRECTOR_ID=d.DIRECTOR_ID " +
                "where lower(f.NAME) like ? or lower(d.NAME) like ? " +
                "group by f.FILM_ID " +
                "order by count(fl.FILM_ID) desc";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (makeFilm(rs)), query, query);
    }

    @Override
    public List<Film> searchFilmsByName(String query) {
        String sqlQuery = "select distinct f.*, " +
                "m.NAME, " +
                "count(fl.FILM_ID)  " +
                "from FILMS f " +
                "left join MPA m on f.MPA_ID = m.MPA_ID " +
                "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                "where lower(f.NAME) like ? " +
                "group by f.FILM_ID " +
                "order by count(fl.FILM_ID) desc ";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (makeFilm(rs)), query);
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        String sqlQuery = "select distinct f.*, " +
                "m.NAME, " +
                "count(fl.FILM_ID)  " +
                "from FILMS f " +
                "left join MPA m on f.MPA_ID = m.MPA_ID " +
                "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                "left join FILM_DIRECTORS FD on f.FILM_ID = FD.FILM_ID " +
                "left join DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "where lower(d.NAME) like ? " +
                "group by f.FILM_ID " +
                "order by count(fl.FILM_ID) desc ";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (makeFilm(rs)), query);
    }

    private void addFilmParameters(Film film, int filmId) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            StringBuilder insertGenresQuery = new StringBuilder("insert into FILM_GENRES (FILM_ID, GENRE_ID) VALUES ");
            int i = 0;
            for (Genre g : film.getGenres()) {
                if (i != 0) insertGenresQuery.append(", ");
                else i = 1;
                insertGenresQuery.append("(").append(filmId).append(",").append(g.getId()).append(")");
            }
            jdbcTemplate.update(insertGenresQuery.toString());
        }

        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            StringBuilder insertLikesQuery = new StringBuilder("insert into FILM_LIKES (FILM_ID, USER_ID) VALUES ");
            int i = 0;
            for (Integer user_id : film.getLikes()) {
                if (i != 0) insertLikesQuery.append(", ");
                else i = 1;
                insertLikesQuery.append("(").append(filmId).append(",").append(user_id).append(")");
            }
            jdbcTemplate.update(insertLikesQuery.toString());
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            StringBuilder insertDirectorsQuery =
                    new StringBuilder("insert into FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ");
            int i = 0;
            for (Director d : film.getDirectors()) {
                if (i != 0) insertDirectorsQuery.append(", ");
                else i = 1;
                insertDirectorsQuery.append("(").append(filmId).append(",").append(d.getId()).append(")");
            }
            jdbcTemplate.update(insertDirectorsQuery.toString());
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(Objects.requireNonNull(rs.getDate("RELEASE_DATE")).toLocalDate())
                .duration(rs.getInt("DURATION"))
                .likes(getLikes(rs.getInt("FILM_ID")))
                .mpa(mpaDao.getMpaById(rs.getInt("MPA_ID")).get())
                .directors(directorDao.listDirectorByFilmId(rs.getInt("FILM_ID")))
                .genres(genreDao.listGenreByFilmId(rs.getLong("FILM_ID")))
                .build();
    }

    private Set<Integer> getLikes(int filmId) {
        String sql = "select USER_ID from FILM_LIKES where FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        return values;
    }
}
