package uk.ac.abertay.tvtracker.TheTVDB;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Task for fetching the JWT Token needed for API usage
 */
class TokenTask extends AsyncTask<String, Void, Response> {
    private ResponseInterface callback = null;

    /**
     * Fetch the JWT Token for API usage
     * params[0] = API URL for JWT Token call
     * params[1] = API KEY
     * params[2] = USER KEY
     * params[3] = USERNAME
     * @param params Paramters
     * @return Response
     */
    @Override
    protected Response doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        Response resp = new Response();

        try {
            url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(10_000);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            String request = "{\"apikey\" : \"" + params[1] + "\", \"userkey\" : \"" + params[2] + "\", \"username\" : \"" + params[3] + "\" }";
            conn.setFixedLengthStreamingMode(request.getBytes().length);
            conn.setRequestProperty("Content-type", "application/json");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            resp.set_response_code(conn.getResponseCode());

            if (resp.get_response_code() == 200) {
                resp.set_response(conn.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return resp;
    }

    /**
     * Pass the token back to the API
     * @param response Response
     */
    protected void onPostExecute(Response response) {
        callback.set_token(response);
    }

    /**
     * Set the callback
     * @param callback Callback
     */
    public void set_callback(ResponseInterface callback) {
        this.callback = callback;
    }
}

