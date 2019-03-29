package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.abertay.tvtracker.FileHandler;

public class PosterTask extends AsyncTask<String, Void, Poster> {
    public ResponseInterface callback = null; //TODO: Make private and use setter

    @Override
    protected Poster doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        HttpURLConnection conn_post = null;
        Response resp = new Response();
        Poster poster = new Poster();
        int response_code = 0;
        int show_id = Integer.parseInt(params[2]);
        poster.set_id(show_id);

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
                    JSONObject rating = (JSONObject) p.getJSONObject("ratingsInfo");
                    if (rating.getDouble("average") > highest_rated) {
                        poster_path = "https://www.thetvdb.com/banners/" + p.getString("thumbnail");
                        highest_rated = rating.getDouble("average");
                    }
                }
                //TODO: Use highest rated poster, rather than the first.

                url = new URL(poster_path);
                conn_post = (HttpURLConnection) url.openConnection();
                conn_post.setDoInput(true);
                conn_post.setConnectTimeout(7_000);
                conn_post.setReadTimeout(7_000);
                conn_post.connect();

                InputStream input = conn_post.getInputStream();
                Bitmap bp = BitmapFactory.decodeStream(input);
                poster.set_bitmap(bp);
                FileHandler.save_to_external_storage(bp, params[3]);
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
        return poster;
    }

    protected void onPostExecute(Poster poster) {
        //callback.show_poster_results(poster);
    }
}
