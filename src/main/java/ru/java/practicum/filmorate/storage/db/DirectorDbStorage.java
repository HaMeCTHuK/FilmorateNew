package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getSortedDirectorListByYear(Long directorId) {
        return null;
    }

    @Override
    public List<Film> getSortedDirectorListByLikes(Long directorId) {
        return null;
    }


    @Override
    public Director create(Director data) {
        return null;
    }

    @Override
    public Director update(Director data) {
        return null;
    }

    @Override
    public List<Director> getAll() {
        return null;
    }

    @Override
    public Director get(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

}
