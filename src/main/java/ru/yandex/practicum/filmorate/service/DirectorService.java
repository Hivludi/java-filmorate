package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {
    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Optional<Director> getDirectorById(int directorId) {
        return directorDao.getDirectorById(directorId);
    }

    public List<Director> listDirector() {
        return directorDao.listDirector();
    }

    public Optional<Director> create(Director director) {
        return directorDao.create(director);
    }

    public Optional<Director> update(Director director) {
        return directorDao.update(director);
    }

    public void delete(int directorId) {
        directorDao.delete(directorId);
    }
}
