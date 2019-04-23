package uk.ac.abertay.tvtracker;

import android.database.Cursor;
import android.graphics.Bitmap;

public class Episode {
    private final int episode_id;
    private final int series_id;
    private final int season_number;
    private final int episode_number;
    private final String name;
    private final String aired;
    private final String overview;
    private final int last_updated;
    private final String file_name;
    private final String imdb_id;
    private final double site_rating;
    private final boolean watched;

    /**
     * Constructor takes a Cursor from the DatabaseHelper class.
     * @param result
     */
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
        return name.equals("null") ? "TBA" : name;
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

    public Bitmap get_image() {
        return FileHandler.load_from_external_storage(file_name);
    }

    public String get_season_episode() {
        return "S" + (season_number < 10 ? "0" : "") + season_number +
                "E" + (episode_number < 10 ? "0" : "") + episode_number;
    }
}
