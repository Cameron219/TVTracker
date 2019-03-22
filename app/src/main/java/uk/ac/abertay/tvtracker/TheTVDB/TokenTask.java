package uk.ac.abertay.tvtracker.TheTVDB;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TokenTask extends AsyncTask<String, Void, Response> {
    public ResponseInterface callback = null; //TODO: Make private and use a setter

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
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

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
        }

        return resp;
    }

    protected void onPostExecute(Response response) {
        // TODO: Return raw token (as opposed to the entire response)
        callback.fetch_result(response.get_response_code(), response.get_response());
    }
}

