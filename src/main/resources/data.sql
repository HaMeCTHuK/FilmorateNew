merge INTO MPARating (id, rating_name)
            values (1, 'G'),
                   (2, 'PG'),
                   (3, 'PG-13'),
                   (4, 'R'),
                   (5, 'NC-17');

merge INTO GENRES (id, genre_name)
            values  (1, 'Комедия'),
                    (2, 'Драма'),
                    (3, 'Мультфильм'),
                    (4, 'Триллер'),
                    (5, 'Документальный'),
                    (6, 'Боевик');

merge INTO EVENTTYPES (id, name)
            values  (1, 'LIKE'),
                    (2, 'REVIEW'),
                    (3, 'FRIEND');

merge INTO EVENTOP (id, name)
            values  (1, 'ADD'),
                    (2, 'REMOVE'),
                    (3, 'UPDATE');