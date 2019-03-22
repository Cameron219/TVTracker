package uk.ac.abertay.tvtracker.TheTVDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Response {
    private int response_code;
    private String response;

    Response() {
        this.response_code = 0;
        this.response = "";
    }

    public void set_response_code(int response_code) {
        this.response_code = response_code;
    }

    public void set_response(String response) {
        this.response = response;
    }

    public void set_response(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append('\n');
        }

        this.response = result.toString();
    }

    public int get_response_code() {
        return response_code;
    }

    public String get_response() {
        return response;
    }
}
