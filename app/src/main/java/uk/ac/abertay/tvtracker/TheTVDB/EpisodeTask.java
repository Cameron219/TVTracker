package uk.ac.abertay.tvtracker.TheTVDB;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.abertay.tvtracker.Episode;

/**
 * Fetch all episode information about a given series.
 * Due to API restriction, one API call only returns 100 episodes
 * This will handle that and continue making API calls for the following episodes
 * Until there are no more to be fetched.
 * With shows with a substantial number of episodes, this can take a while to run.
 */
class EpisodeTask extends AsyncTask<String, Void, JSONArray> {
    private ResponseInterface callback = null;
    private ProgressBar spinner;

    /**
     * Set the progress bar spinner
     * @param spinner ProgressBar
     */
    public EpisodeTask(ProgressBar spinner) {
        this.spinner = spinner;
    }

    /**
     * Set loading spinner visible while task is running
     */
    @Override
    protected void onPreExecute() {
        spinner.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    /**
     * Async Task that fetches all episode information about a series
     * Continues to make API calls for more episodes while there is more pages
     * param[0] = JWT Token for API
     * param[1] = URL for API call, WITHOUT page number
     * @param params Parameters
     * @return JSONArray from API containing episodes
     */
    @Override
    protected JSONArray doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        Response resp = new Response();
        JSONArray data = new JSONArray();
        int current_page = 1;
        int last_page = 1;

        do {
            try {
                url = new URL(params[1] + "?page=" + current_page);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15_000);
                conn.setReadTimeout(10_000);

                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + params[0]);

                int response_code = conn.getResponseCode();
                resp.set_response_code(response_code);

                if(response_code == 200) {
                    resp.set_response(conn.getInputStream());
                    JSONObject response = new JSONObject(resp.get_response());
                    JSONObject links = response.getJSONObject("links");
                    last_page = links.getInt("last");

                    JSONArray page_data = response.getJSONArray("data");
                    for(int i = 0; i < page_data.length(); i++ ) {
                        data.put(page_data.get(i));
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (JSONException e) {
                e.printStackTrace();
                break;
            } finally {
                conn.disconnect();
            }
            current_page++;
        } while(current_page <= last_page);

        return data;
    }

    /**
     * Method called when AsyncTask is finished.
     * Recieves Episode data, passes it back to callback class (TVDB_API)
     * Hides the progress bar spinner
     * @param data Episode Data
     */
    protected void onPostExecute(JSONArray data) {
        spinner.setVisibility(View.GONE);
        callback.insert_episodes(data);
    }

    /**
     * Set callback class (TVDB_API)
     * @param callback Callback
     */
    public void set_callback(ResponseInterface callback) {
        this.callback = callback;
    }
}
