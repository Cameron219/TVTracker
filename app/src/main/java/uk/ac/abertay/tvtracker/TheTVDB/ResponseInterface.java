package uk.ac.abertay.tvtracker.TheTVDB;

public interface ResponseInterface {
    void fetch_result(int response_code, String response);
    void show_search_results(Response response);
    void show_poster_results(Poster response);
}