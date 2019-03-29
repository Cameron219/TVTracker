package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BannerTask extends AsyncTask<String, Void, Banner> {
    public ResponseInterface callback = null; //TODO: Make private and use setter

    @Override
    protected Banner doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        Response resp = new Response();
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

    protected void onPostExecute(Banner banner) {
        callback.show_banner(banner);
    }
}
