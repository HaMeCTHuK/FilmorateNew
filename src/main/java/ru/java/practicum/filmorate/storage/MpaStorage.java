package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage extends AbstractStorage<Mpa> {

    @Override
    default List<Mpa> getAll() {
        return null;
    }

    @Override
    default Mpa get(Long id) {
        return null;
    }
}
