package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.DirectorStorage;


import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DirectorService extends AbstractService<Director> {

    private final DirectorStorage storage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage, DirectorStorage storage) {
        this.storage = storage;
        this.abstractStorage = directorStorage;
    }

    public List<Film> getSortedDirectorList(Long directorId, String sortBy) {
        validateParameter(directorId);
        log.info("Получаем список фильмов режиссера отсортированных по {}", sortBy);
        List<Film> films = new ArrayList<>();

        if ("year".equals(sortBy)) {
            // Сортировка по году выпуска
            films = storage.getSortedDirectorListByYear(directorId);

        } else if ("likes".equals(sortBy)) {
            // Сортировка по количеству лайков
            films = storage.getSortedDirectorListByLikes(directorId);

        } else {
            throw new IncorrectParameterException("Некорректное задание сортировки");
        }

        return films;
    }


    @Override
    public void validateParameters(Long id, Long otherId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Director create(Director data) {
        return abstractStorage.create(data);
    }

    @Override
    public Director update(Director data) {
        return abstractStorage.update(data);
    }

    @Override
    public List<Director> getAll() {
        return abstractStorage.getAll();
    }

    @Override
    public Director getData(Long id) {
        return abstractStorage.get(id);
    }

    @Override
    public void validateParameter(Long directorId) {
        if (directorId == null) {
            throw new IncorrectParameterException("Некорректные параметры поля, проверь null");
        }
        if (getData(directorId) == null) {
            throw new DataNotFoundException("Режисера с айди нет" + directorId);
        }
    }

    @Override
    public void validate(Director data) {
        throw new UnsupportedOperationException();
    }

}
