package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDao genreDao;

    public Optional<Genre> getGenreById(int genreId) {
        if (genreDao.getGenreById(genreId).isPresent()) return genreDao.getGenreById(genreId);
        else throw new ObjectNotFoundException(String.format("Жанр с идентификатором %s не найден", genreId));
    }

    public List<Genre> listGenre() {
        return genreDao.listGenre();
    }
}
