package ru.yandex.practicum.filmorate.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDao mpaDao;

    public Optional<Mpa> getMpaById(int mpaId) {
        if (mpaDao.getMpaById(mpaId).isPresent()) return mpaDao.getMpaById(mpaId);
        else throw new ObjectNotFoundException(String.format("Рейтинг с идентификатором %s не найден", mpaId));
    }

    public List<Mpa> listMpa() {
        return mpaDao.listMpa();
    }
}
