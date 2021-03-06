package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import uk.ac.abertay.tvtracker.BuildConfig;
import uk.ac.abertay.tvtracker.EpisodeActivity;
import uk.ac.abertay.tvtracker.SearchActivity;
import uk.ac.abertay.tvtracker.SeriesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles all API requests sent to the TVDB API
 * This class makes use of the singleton design pattern,
 * which means it initializes and holds a reference to itself.
 * This allows there to only be one instance of the object throughout the application.
 * This is required as many different activities require access to the API,
 * and a token is generated that gives this application access to the API for 24 hours.
 * If a new instance of the API was declared / initialized on each activity,
 * that token would be lost and a new one fetched, which is wasteful and not required.
 */
public class TVDB_API implements ResponseInterface {
    // Instance of itself.
    private static TVDB_API INSTANCE = null;

    /**
     * These variables are stored within the gradle properties of the app.
     * This prevents them from being present on git, while still allowing the application to access them.
     * API_KEY:     Key provided by TVDB API
     * USER_KEY:    User Key for TVDB Account
     * USERNAME:    Username of TVDB Account
     */
    private static final String API_KEY = BuildConfig.TVDB_API_KEY;
    private static final String USER_KEY = BuildConfig.TVDB_USER_KEY;
    private static final String USERNAME = BuildConfig.TVDB_USERNAME;

    //URL to the API
    private static final String API_URL = "https://api.thetvdb.com/";

    /**
     * This token is required to send any request to the API
     * It is generated by sending the above API_KEY, USER_KEY and USERNAME to the server.
     * This is done by TokenTask
     */
    private String JWT_Token = "";

    /**
     * Store references to activities that call the API, as some API methods require callbacks to the activity when the request is done.
     */
    private SearchActivity search_activity;
    private SeriesActivity series_activity;
    private EpisodeActivity episode_activity;

    // Last search term. Used for re-searching if token auth failed.
    private String last_search_term = null;

    /**
     * Default PRIVATE constructor
     * Private so it can only be instantiated by itself.
     * Can be fetched with getInstance()
     *
     * Fetches the Token required for API calls.
     */
    private TVDB_API() {
        fetch_jwt_token();
    }

    /**
     * Call the ASyncTask TokenTask, which will return a JWT Token that allows for API requests to be made.
     * This requires the API_KEY, USER_KEY and USERNAME to be set and valid.
     */
    private void fetch_jwt_token() {
        TokenTask task = new TokenTask();
        task.set_callback(this);
        task.execute(API_URL + "login", API_KEY, USER_KEY, USERNAME);

        if(last_search_term != null) {
            //SearchTask s_task = new SearchTask();
            //s_task.set_callback(this);
            //s_task.execute(JWT_Token, API_URL + "search/series?name=" + last_search_term);
        }
    }

    /**
     * Check if the token is set
     * @return true if the token is set, false otherwise.
     */
    private boolean is_token_set() {
        return JWT_Token.length() > 0;
    }

    /**
     * Callback for the TokenTask to set the token returned
     * Will output error message if unexpected response is returned from the server
     * @param response Response of the request
     */
    @Override
    public void set_token(Response response) {
        if(response.get_response_code() == 200) {
            try {
                JSONObject resp = new JSONObject(response.get_response());
                Log.d("Token", resp.getString("token"));
                JWT_Token = resp.getString("token");
            } catch (JSONException e) {
                Log.e("JSON", "Unable to parse JSON response:\n" + response);
                e.printStackTrace();
            }
        } else if(response.get_response_code() == 401) {
            Log.e("TVDB", "Credentials provided are not authorized. Please check them");
        } else {
            //Likely internet issues.
            Log.e("TVDB", "TVDB response returned " + response.get_response_code() + ". Failed to get API token");
            Log.d("TVDB", "Response: " + response.get_response());
        }
    }

    /**
     * Search for a series
     * @param search_term Series to search for
     * @param search_actitivy Search Activity instance
     * @return ASyncTask performing the search.
     */
    public AsyncTask search_for_series(String search_term, SearchActivity search_actitivy, ProgressBar spinner) {
        if(!is_token_set()) fetch_jwt_token();
        this.search_activity = search_actitivy;

        SearchTask task = new SearchTask(spinner);
        task.set_callback(this);
        task.execute(JWT_Token, API_URL + "search/series?name=" + search_term);

        return task;
    }

    /**
     * Callback for SearchTask to display the search results in the Search Activity.
     * @param response Response of the server
     */
    public void show_search_results(Response response) {
        if(response.get_response_code() == 401) {
            fetch_jwt_token();
        } else {
            last_search_term = "";
            search_activity.display_results(response);
        }
    }

    /**
     * Fetch the poster of a series.
     * @param id ID of the series
     * @param slug Slug of the series. The poster is then saved as {slug}.png
     * @return The ASyncTask PosterTask
     */
    public AsyncTask get_poster(int id, String slug, ProgressBar spinner) {
        PosterTask task = new PosterTask(spinner);
        task.execute(JWT_Token, API_URL + "series/" + id + "/images/query?keyType=poster", "" + id, slug);

        return task;
    }

    /**
     * Get the info about a series
     * @param series_id ID of the series
     * @param search_activity
     */
    public void get_series(int series_id, SearchActivity search_activity, ProgressBar spinner) {
        this.search_activity = search_activity;
        SeriesTask task = new SeriesTask(spinner);
        task.set_callback(this);
        task.execute(JWT_Token, API_URL + "series/" + series_id);
    }

    /**
     * Callback for the get series task.
     * Passes the data back to the activity that called it.
     * @param series JSONObject containing data returned.
     */
    @Override
    public void insert_series(JSONObject series) {
        search_activity.insert_series(series);
    }

    /**
     * Calls the ASyncTask Episode Task. This fetches the episode information about a given series.
     * @param series_id ID of the series
     * @param series_activity Activity his method was called for.
     */
    public void get_episodes(int series_id, SeriesActivity series_activity, ProgressBar spinner) {
        this.series_activity = series_activity;
        EpisodeTask task = new EpisodeTask(spinner);
        task.set_callback(this);
        task.execute(JWT_Token, API_URL + "series/" + series_id + "/episodes");
    }

    /**
     * Callback for the Episode Task
     * Passes the data back to the activity that called it (SearchActivity)
     * @param data JSONArray of episodes
     */
    public void insert_episodes(JSONArray data) {
        series_activity.insert_episodes(data);
    }

    /**
     * Calls the ASyncTask Banner Task. This fetches the banner for a given series.
     * @param path URL to the banner
     * @param id ID of the show
     * @param search_actitivy Search Activity that this is called from.
     * @return
     */
    public AsyncTask get_banner(String path, int id, SearchActivity search_actitivy) {
        this.search_activity = search_actitivy;
        BannerTask task = new BannerTask();
        task.set_callback(this);
        task.execute(path, "" + id);

        return task;
    }

    /**
     * Callback for banner task. Passes the banner object back to the search activity
     * @param banner Banner bitmap and series id
     */
    public void show_banner(Banner banner) {
        search_activity.update_banner(banner);
    }

    /**
     * Calls the AsyncTask that fetches an episode image.
     * @param filename Name / path of the file
     * @param episode_activity Activity called from.
     */
    public void get_image(String filename, EpisodeActivity episode_activity, ProgressBar spinner) {
        this.episode_activity = episode_activity;
        EpisodeImageTask task = new EpisodeImageTask(spinner);
        task.set_callback(this);
        task.execute("https://www.thetvdb.com/banners/" + filename, filename);
    }

    /**
     * Callback for the Episode Image Task. Passes the image back to the episode activity
     * @param image Episode image
     */
    public void show_image(Bitmap image) {
        episode_activity.update_image(image);
    }

    /**
     * If an instance exists of the current class, return it.
     * If one does not exist then create one and return that.
     * @return Instance of TVDB_API
     */
    public static TVDB_API getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TVDB_API();
        }
        return INSTANCE;
    }
}
