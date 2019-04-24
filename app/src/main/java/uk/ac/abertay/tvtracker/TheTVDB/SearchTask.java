package uk.ac.abertay.tvtracker.TheTVDB;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Task to search the TVDB API for series
 */
class SearchTask extends AsyncTask<String, Void, Response> {
    private ResponseInterface callback = null;
    private ProgressBar spinner;

    public SearchTask(ProgressBar spinner) {
        this.spinner = spinner;
    }

    @Override
    protected void onPreExecute() {
        spinner.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    /**
     * Async method to fetch search results for series
     * params[0] = JWT Token
     * params[1] = URL for search API (includes search term)
     * @param params Parameters
     * @return Response
     */
    @Override
    protected Response doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
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
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return resp;
    }

    /**
     * Callback to TVDB_API with result
     * @param response Response
     */
    protected void onPostExecute(Response response) {
        spinner.setVisibility(View.GONE);
        callback.show_search_results(response);
    }

    /**
     * Set the callback (TVDB_API)
     * @param callback Callback
     */
    public void set_callback(ResponseInterface callback) {
        this.callback = callback;
    }
}
