package uk.ac.abertay.tvtracker;

import android.database.Cursor;
import android.graphics.Bitmap;

public class Episode {
    private int episode_id;
    private int series_id;
    private int season_number;
    private int episode_number;
    private String name;
    private String aired;
    private String overview;
    private int last_updated;
    private String file_name;
    private String imdb_id;
    private double site_rating;
    private boolean watched;
    private boolean notification;

    Episode(Cursor result) {
        this.episode_id = result.getInt(0);
        this.series_id = result.getInt(1);
        this.season_number = result.getInt(2);
        this.episode_number = result.getInt(3);
        this.name = result.getString(4);
        this.aired = result.getString(5);
        this.overview = result.getString(6);
        this.last_updated = result.getInt(7);
        this.file_name = result.getString(8);
        this.imdb_id = result.getString(9);
        this.site_rating = result.getDouble(10);
        this.watched = result.getInt(11) == 1;
        this.notification = result.getInt(12) == 1;
    }

    public int get_episode_id() {
        return episode_id;
    }

    public int get_series_id() {
        return series_id;
    }

    public int get_season_number() {
        return season_number;
    }

    public int get_episode_number() {
        return episode_number;
    }

    public String get_name() {
        return name;
    }

    public String get_aired() {
        return aired;
    }

    public String get_overview() {
        return overview;
    }

    public int get_last_updated() {
        return last_updated;
    }

    public String get_file_name() {
        return file_name;
    }

    public String get_imdb_id() {
        return imdb_id;
    }

    public double get_site_rating() {
        return site_rating;
    }

    public boolean is_watched() {
        return watched;
    }

    public boolean is_notification() {
        return notification;
    }

    public Bitmap get_image() {
        return FileHandler.load_from_external_storage(file_name);
    }
}
