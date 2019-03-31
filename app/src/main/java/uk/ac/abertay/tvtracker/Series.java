package uk.ac.abertay.tvtracker;

import android.database.Cursor;
import android.graphics.Bitmap;


public class Series {
    private int id;
    private String name;
    private String poster;
    private String banner;
    private String status;
    private String network;
    private String first_aired;
    private String overview;
    private int last_updated;
    private String content_rating;
    private String imdb_id;
    private double site_rating;
    private String slug;

    Series(Cursor data) {
        this.id = data.getInt(0);
        this.name = data.getString(1);
        this.poster = data.getString(2);
        this.banner = data.getString(3);
        this.status = data.getString(4);
        this.network = data.getString(5);
        this.first_aired = data.getString(6);
        this.overview = data.getString(7);
        this.last_updated = data.getInt(8);
        this.content_rating = data.getString(9);
        this.imdb_id = data.getString(10);
        this.site_rating = data.getDouble(11);
        this.slug = data.getString(12);
    }

    Series(int id, String name, String poster) {
        this.id = id;
        this.name = name;
        this.poster = poster;
    }

    Series(int id, String name, String overview, String poster) {
        this.id = id;
        this.name = name;
        this.overview = overview;
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

    public String get_banner_path() {
        return this.banner;
    }

    public Bitmap get_banner() {
        return FileHandler.load_from_external_storage("banner/" + banner);
    }

    public String get_status() {
        return this.status;
    }

    public String get_network() {
        return this.network;
    }

    public String get_first_aired() {
        return this.first_aired;
    }

    public String get_overview() {
        return this.overview;
    }

    public int get_last_updated() {
        return this.last_updated;
    }

    public String get_content_rating() {
        return this.content_rating;
    }

    public String get_imdb_id() {
        return this.imdb_id;
    }

    public double get_site_rating() {
        return this.site_rating;
    }

    public String get_slug() {
        return this.slug;
    }
}
