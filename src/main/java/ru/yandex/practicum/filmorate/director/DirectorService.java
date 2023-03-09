package ru.yandex.practicum.filmorate.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDao directorDao;

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
