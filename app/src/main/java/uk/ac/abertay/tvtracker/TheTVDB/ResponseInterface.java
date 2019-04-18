package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ResponseInterface {
    void set_token(Response response);
    void show_search_results(Response response);
    void insert_series(JSONObject series);
    void show_banner(Banner banner);
    void insert_episodes(JSONArray data);
    void show_image(Bitmap image);
}
