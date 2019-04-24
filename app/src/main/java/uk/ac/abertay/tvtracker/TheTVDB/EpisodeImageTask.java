package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.abertay.tvtracker.FileHandler;

/**
 * AsyncTask to fetch the image of an episode
 */
class EpisodeImageTask extends AsyncTask<String, Void, Bitmap> {
    private ResponseInterface callback = null;
    private ProgressBar spinner;

    public EpisodeImageTask(ProgressBar spinner) {
        this.spinner = spinner;
    }

    @Override
    protected void onPreExecute() {
        spinner.setVisibility(View.VISIBLE);
    }

    /**
     * The Async method for EpisodeImageTask
     * Param[0] = Full URL of the episode image
     * Param[1] = Path to save episode image to
     * @param params Parameters
     * @return Episode Image Bitmap, null if no episode image found
     */
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

    /**
     * Called when doInBackground is done
     * Callback to calling class (TVDB_API)
     * @param image Bitmap image
     */
    @Override
    protected void onPostExecute(Bitmap image) {
        spinner.setVisibility(View.GONE);
        callback.show_image(image);
    }

    /**
     * Set the callback class (TVDB_API)
     * @param callback Callback
     */
    public void set_callback(ResponseInterface callback) {
        this.callback = callback;
    }
}
