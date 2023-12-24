/*
package ru.java.practicum.filmorate.controller;

import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.BaseUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseController<T extends BaseUnit> {
    private final Map<Long, T> storage = new HashMap<>();
    private long generateId;

    public T create(T data) {

        validate(data);
        data.setId(++generateId);
        storage.put(data.getId(), data);
        return data;
    }

    public T update(T data) {

        validate(data);
        if (!storage.containsKey(data.getId())) {
            throw new ValidationException("Обновление не выполнено, id отсутствует в хранилище");
        }

        storage.put(data.getId(), data);
        return data;
    }

    public List<T> getData() {

        return new ArrayList<>(storage.values());
    }

    public abstract void validate(T data);
}
*/
