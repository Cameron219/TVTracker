package uk.ac.abertay.tvtracker;

import android.database.Cursor;
import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class Series implements Serializable {
    private int id;
    private String name = "";
    private String poster = "";
    private String banner = "";
    private String status = "";
    private String network = "";
    private String first_aired = "";
    private String overview = "";
    private int last_updated;
    private String content_rating = "";
    private String imdb_id = "";
    private double site_rating;
    private String slug = "";
    private String airsTime = "";
    private String runtime = "";
    private Episode next_episode = null;
    private Bitmap banner_image = null;

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
        this.airsTime = data.getString(13);
        this.runtime = data.getString(14);
    }

    Series(JSONObject data) {
        try {
            this.id = data.getInt("id");
            this.name = data.getString("seriesName");
            this.overview = data.getString("overview");
            this.banner = data.getString("banner");
            this.status = data.getString("status");
            this.network = data.getString("network");
            this.slug = data.getString("slug");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    Series(int id) {
        this.id = id;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public void set_poster(String poster) {
        this.poster = poster;
    }

    public void set_banner(String banner) {
        this.banner = banner;
    }

    public void set_status(String status) {
        this.status = status;
    }

    public void set_network(String network) {
        this.network = network;
    }

    public void set_first_aired(String first_aired) {
        this.first_aired = first_aired;
    }

    public void set_overview(String overview) {
        this.overview = overview;
    }

    public void set_last_updated(int last_updated) {
        this.last_updated = last_updated;
    }

    public void set_content_rating(String content_rating) {
        this.content_rating = content_rating;
    }

    public void set_imdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public void set_site_rating(double site_rating) {
        this.site_rating = site_rating;
    }

    public void set_slug(String slug) {
        this.slug = slug;
    }

    public void set_airstime(String airsTime) {
        this.airsTime = airsTime;
    }

    public void set_runtime(String runtime) {
        this.runtime = runtime;
    }

    public void set_banner_image(Bitmap bp) {
        this.banner_image = bp;
    }

    public String get_airstime() {
        return this.airsTime;
    }

    public String get_runtime() {
        return this.runtime;
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

    public Episode get_next_episode() {
        return next_episode;
    }

    public void set_next_episode(Episode episode) {
        this.next_episode = episode;
    }

    public Bitmap get_banner_image() {
        return this.banner_image;
    }
}
