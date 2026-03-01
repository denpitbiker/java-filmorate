package ru.yandex.practicum.filmorate.tool.comparison;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmLikesComparator implements Comparator<Film> {

    private final boolean isReverse;

    public FilmLikesComparator(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public FilmLikesComparator() {
        this.isReverse = false;
    }

    @Override
    public int compare(Film o1, Film o2) {
        int likesDiff = o1.getLikes().size() - o2.getLikes().size();
        if (isReverse) {
            return -likesDiff;
        } else {
            return likesDiff;
        }
    }
}
