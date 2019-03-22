package uk.ac.abertay.tvtracker.TheTVDB;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SearchTask extends AsyncTask<String, Void, Response> {
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
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return resp;
    }

    protected void onPostExecute(Response response) {
        callback.show_search_results(response);
    }
}
