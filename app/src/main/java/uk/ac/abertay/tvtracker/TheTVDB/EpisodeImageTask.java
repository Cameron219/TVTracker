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

public class EpisodeImageTask extends AsyncTask<String, Void, Bitmap> {
    public ResponseInterface callback = null;

    @Override
    protected Bitmap doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        Bitmap image = null;

        try {
            url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setConnectTimeout(7_000);
            conn.setReadTimeout(7_000);
            conn.connect();

            if(conn.getResponseCode() == 200) {
                InputStream input = conn.getInputStream();
                image = BitmapFactory.decodeStream(input);
                FileHandler.save_to_external_storage(image, params[1]);
                input.close();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        callback.show_image(image);
    }
}
