package uk.ac.abertay.tvtracker.TheTVDB;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class EpisodeTask extends AsyncTask<String, Void, JSONArray> {
    public ResponseInterface callback = null;

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

    protected void onPostExecute(JSONArray data) {
        callback.insert_episodes(data);
    }
}
