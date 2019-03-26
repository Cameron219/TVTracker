package uk.ac.abertay.tvtracker.TheTVDB;

import org.json.JSONObject;

public interface ResponseInterface {
    void fetch_result(int response_code, String response);
    void show_search_results(Response response);
    void show_poster_results(Poster response);
    void insert_series(JSONObject series);
}
