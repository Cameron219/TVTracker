package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.abertay.tvtracker.FileHandler;

/**
 * AsyncTask to fetch the banner image from the TVDB API.
 */
class BannerTask extends AsyncTask<String, Void, Banner> {
    private ResponseInterface callback = null;

    /**
     * The Async method to fetch the banner image
     * Param[0] = Full URL of the banner image
     * Param[1] = ID of the series
     * @param params Parameters
     * @return A Banner object, containing the series ID and the banner bitmap
     */
    @Override
    protected Banner doInBackground(String... params) {
        URL url;
        HttpURLConnection conn;
        Banner banner = new Banner();
        int show_id = Integer.parseInt(params[1]);
        banner.set_id(show_id);

        try {
            url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(7_000);
            conn.setReadTimeout(7_000);
            conn.connect();

            InputStream input = conn.getInputStream();
            Bitmap bp = BitmapFactory.decodeStream(input);
            input.close();

            if(conn.getResponseCode() == 200) {
                banner.set_banner(bp);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return banner;
    }

    /**
     * Once banner has been fetched, callback to the API to handle the response.
     * @param banner Object returned by doInBackground
     */
    protected void onPostExecute(Banner banner) {
        callback.show_banner(banner);
    }

    /**
     * Set the callback class (TVDB_API)
     * @param callback Callback class
     */
    public void set_callback(ResponseInterface callback) {
        this.callback = callback;
    }
}
