package uk.ac.abertay.tvtracker.TheTVDB;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.abertay.tvtracker.SearchActivity;

class SeriesTask extends AsyncTask<String, Void, Response> {
    public ResponseInterface callback = null; //TODO: Make private and use setter
    private final ProgressDialog dialog;
    //TODO: Replace with spinner

    public SeriesTask(SearchActivity activity) {
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Downloading Series, please wait.");
        dialog.show();
    }

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

    protected void onPostExecute(Response response) {
        if(dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            JSONObject series = new JSONObject(response.get_response());
            callback.insert_series(series.getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
