package uk.ac.abertay.tvtracker.TheTVDB;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SeriesTask extends AsyncTask<String, Void, Response> {
    public ResponseInterface callback = null; //TODO: Make private and use setter
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resp;
    }

    protected void onPostExecute(Response response) {
        try {
            JSONObject series = new JSONObject(response.get_response());
            callback.insert_series(series.getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
