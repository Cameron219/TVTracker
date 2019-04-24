package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Used to callback from AsyncTasks to the class they were called from (TVDB_API)
 */
public interface ResponseInterface {
    /**
     * Set the JWT Token
     * @param response Response
     */
    void set_token(Response response);

    /**
     * Show the search results
     * @param response Response
     */
    void show_search_results(Response response);

    /**
     * Insert series info into DB
     * @param series Series data
     */
    void insert_series(JSONObject series);

    /**
     * Show the banner image
     * @param banner Banner image
     */
    void show_banner(Banner banner);

    /**
     * Insert the episode data into the DB
     * @param data Episode data
     */
    void insert_episodes(JSONArray data);

    /**
     * Show the episode image
     * @param image Episode image
     */
    void show_image(Bitmap image);
}
