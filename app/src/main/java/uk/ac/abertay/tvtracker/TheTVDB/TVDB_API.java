package uk.ac.abertay.tvtracker.TheTVDB;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.ac.abertay.tvtracker.BuildConfig;
import uk.ac.abertay.tvtracker.DatabaseHelper;
import uk.ac.abertay.tvtracker.SearchActivity;
import uk.ac.abertay.tvtracker.SeriesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TVDB_API implements ResponseInterface {
    private static TVDB_API INSTANCE = null;
    private static final String API_KEY = BuildConfig.TVDB_API_KEY;
    private static final String USER_KEY = BuildConfig.TVDB_USER_KEY;
    private static final String USERNAME = BuildConfig.TVDB_USERNAME;
    private static final String API_URL = "https://api.thetvdb.com/";

    private String JWT_Token = "";

    private SearchActivity search_activity;
    private SeriesActivity series_activity;

    private TVDB_API() {
        fetch_jwt_token();
    }

    public void fetch_jwt_token() {
        TokenTask task = new TokenTask();
        task.callback = this;
        task.execute(API_URL + "login", API_KEY, USER_KEY, USERNAME);
    }

    private boolean is_token_set() {
        return JWT_Token.length() > 0;
    }

    @Override
    public void fetch_result(int response_code, String response) {
        Log.d("JWT", "Response Code: " + response_code);
        Log.d("JWT", "Response: " + response);

        try {
            JSONObject resp = new JSONObject(response);
            Log.d("Token", resp.getString("token"));
            JWT_Token = resp.getString("token");
        } catch (JSONException e) {
            Log.e("JSON", "Unable to parse JSON response:\n" + response);
            e.printStackTrace();
        }
    }

    public String get_jwt_token() {
        return JWT_Token;
    }

    public AsyncTask search_for_show(String search_term, SearchActivity search_actitivy) {
        if(!is_token_set()) fetch_jwt_token();
        this.search_activity = search_actitivy;

        SearchTask task = new SearchTask();
        task.callback = this;
        task.execute(JWT_Token, API_URL + "search/series?name=" + search_term);

        return task;
    }

    public void show_search_results(Response response) {
        ((SearchActivity) search_activity).display_results(response);
    }

    public AsyncTask get_poster(int id, String slug, SearchActivity search_actitivy) {
        this.search_activity = search_actitivy;
        PosterTask task = new PosterTask();
        task.callback = this;
        task.execute(JWT_Token, API_URL + "series/" + id + "/images/query?keyType=poster", "" + id, slug);

        return task;
    }

    @Override
    public void show_poster_results(Poster poster) {
        ((SearchActivity) search_activity).update_poster(poster);
    }

    public void get_series(int series_id, SearchActivity search_activity) {
        this.search_activity = search_activity;
        SeriesTask task = new SeriesTask((SearchActivity) search_activity);
        task.callback = this;
        task.execute(JWT_Token, API_URL + "series/" + series_id);
    }

    @Override
    public void insert_series(JSONObject series) {
        ((SearchActivity) search_activity).insert_series(series);
    }

    public void get_episodes(int series_id, SeriesActivity series_activity) {
        this.series_activity = series_activity;
        EpisodeTask task = new EpisodeTask();
        task.callback = this;
        task.execute(JWT_Token, API_URL + "series/" + series_id + "/episodes");
    }

    public void insert_episodes(JSONArray data) {
        series_activity.insert_episodes(data);
    }

    public AsyncTask get_banner(String path, int id, SearchActivity search_actitivy) {
        this.search_activity = search_actitivy;
        BannerTask task = new BannerTask();
        task.callback = this;
        task.execute(path, "" + id);

        return task;
    }

    public void show_banner(Banner banner) {
        ((SearchActivity) search_activity).update_banner(banner);
    }

    public static TVDB_API getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TVDB_API();
        }
        return INSTANCE;
    }
}
