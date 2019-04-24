package uk.ac.abertay.tvtracker.TheTVDB;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.abertay.tvtracker.SearchActivity;

/**
 * Task to fetch Series data from the TVDB API
 */
class SeriesTask extends AsyncTask<String, Void, Response> {
    private ResponseInterface callback = null;
    private ProgressBar spinner;

    /**
     * Spinner from the activity
     * @param spinner
     */
    public SeriesTask(ProgressBar spinner) {
        this.spinner = spinner;
    }

    /**
     * Show the progressbar spinner
     */
    @Override
    protected void onPreExecute() {
        spinner.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    /**
     * Async method for fetching series data
     * parmas[0] = JWT Token for API
     * params[1] = API URL for series data
     * @param params Parameters
     * @return Response
     */
    @Override
    protected Response doInBackground(String... params) {
        URL url;
        HttpURLConnection conn;
        Response resp = new Response();

        try {
            url = new URL(params[1]);
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
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resp;
    }

    /**
     * Hide the progress bar spinner
     * Callback to API with series data
     * @param response
     */
    protected void onPostExecute(Response response) {
        spinner.setVisibility(View.GONE);
        try {
            JSONObject series = new JSONObject(response.get_response());
            callback.insert_series(series.getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the callback
     * @param callback callback
     */
    public void set_callback(ResponseInterface callback) {
        this.callback = callback;
    }
}
