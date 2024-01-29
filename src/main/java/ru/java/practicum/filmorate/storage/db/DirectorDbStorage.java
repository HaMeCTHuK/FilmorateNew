package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    // Метод получения списка фильмов режисера с сортировкой по году
    @Override
    public List<Film> getSortedDirectorListByYear(Long directorId) {
        String query = "SELECT * FROM films WHERE director_id = ? ORDER BY release_date";
        return jdbcTemplate.query(query, FilmDbStorage::createFilm, directorId);
    }

    // Метод получения списка фильмов режисера с сортировкой по лайкам
    @Override
    public List<Film> getSortedDirectorListByLikes(Long directorId) {
        String query = "SELECT films.*, COUNT(likes.film_id) AS like_count " +
                "FROM films " +
                "LEFT JOIN likes ON films.id = likes.film_id " +
                "WHERE films.director_id = ? " +
                "GROUP BY films.id " +
                "ORDER BY like_count DESC";
        return jdbcTemplate.query(query, FilmDbStorage::createFilm, directorId);
    }

    // Метод создания режиссера в базе данных
    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO DIRECTORS (id, director_name) VALUES (?, ?)";
        jdbcTemplate.update(sql, director.getId(), director.getName());
        return director;
    }

    // Метод обновления режиссера в базе данных
    @Override
    public Director update(Director director) {
        String sql = "UPDATE DIRECTORS SET director_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return get(director.getId());
    }

    // Метод получения всех режиссеров из базы данных
    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, DirectorDbStorage::createDirector);
    }

    // Метод получения режиссера по идентификатору из базы данных
    @Override
    public Director get(Long id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, DirectorDbStorage::createDirector, id);
        if (directors.size() != 1) {
            throw new DataNotFoundException("При получении жанра по id список не равен 1");
        }
        return directors.get(0);
    }

    // Метод удаления режиссера из базы данных
    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM DIRECTORS WHERE id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, id);
        if (affectedRows != 1) {
            throw new DataNotFoundException("При удалении режисера по id количество удаленных строк не равно 1");
        }
    }

    // Вспомогательный метод для создания объекта Director из ResultSet
    static Director createDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
