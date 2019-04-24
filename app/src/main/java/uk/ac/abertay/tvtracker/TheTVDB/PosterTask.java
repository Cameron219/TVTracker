package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.abertay.tvtracker.FileHandler;

/**
 * AsyncTask to fetch the poster image for a series and save it to external storage
 * Fetches the URL via an API call and makes the corresponding call for downloading the image.
 */
class PosterTask extends AsyncTask<String, Void, Void> {
    ProgressBar spinner;

    public PosterTask(ProgressBar spinner) {
        this.spinner = spinner;
    }

    @Override
    protected void onPreExecute() {
        spinner.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    /**
     * The Async method for PosterTask
     * params[0] = JWT Token for API Call
     * params[1] = API URL for poster
     * @param params Parameters
     * @return Void (nothing)
     */
    @Override
    protected Void doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        HttpURLConnection conn_post = null;
        Response resp = new Response();
        int response_code = 0;

        try {
            url = new URL(params[1]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(10_000);

            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + params[0]);

            response_code = conn.getResponseCode();
            resp.set_response_code(response_code);

            if (response_code == 200) {
                InputStream r = conn.getInputStream();
                resp.set_response(r);
                r.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        if (response_code == 200 && !this.isCancelled()) {
            try {
                JSONObject json = new JSONObject(resp.get_response());
                JSONArray data = json.getJSONArray("data");
                double highest_rated = -1;
                String poster_path = "";

                for(int i = 0; i < data.length(); i++) {
                    JSONObject p = (JSONObject) data.get(i);
                    JSONObject rating = p.getJSONObject("ratingsInfo");
                    if (rating.getDouble("average") > highest_rated) {
                        poster_path = "https://www.thetvdb.com/banners/" + p.getString("thumbnail");
                        highest_rated = rating.getDouble("average");
                    }
                }

                url = new URL(poster_path);
                conn_post = (HttpURLConnection) url.openConnection();
                conn_post.setDoInput(true);
                conn_post.setConnectTimeout(7_000);
                conn_post.setReadTimeout(7_000);
                conn_post.connect();

                InputStream input = conn_post.getInputStream();
                Bitmap bp = BitmapFactory.decodeStream(input);
                FileHandler.save_to_external_storage(bp, "/poster/" + params[3] + ".png");
                input.close();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn_post.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        spinner.setVisibility(View.GONE);
        super.onPostExecute(aVoid);
    }
}
