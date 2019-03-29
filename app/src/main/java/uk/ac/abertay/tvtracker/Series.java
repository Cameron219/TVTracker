package uk.ac.abertay.tvtracker;

import android.graphics.Bitmap;

public class Series {
    private int id;
    private String name;
    private String poster;

    Series(int id, String name, String poster) {
        this.id = id;
        this.name = name;
        this.poster = poster;
    }

    public int get_id() {
        return this.id;
    }

    public String get_name() {
        return this.name;
    }

    public String get_poster_path() {
        return this.poster;
    }

    public Bitmap get_poster() {
        return FileHandler.load_from_external_storage("poster/" + poster);
    }
}
