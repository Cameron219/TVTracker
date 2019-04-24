package uk.ac.abertay.tvtracker.TheTVDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Response class that holds the response of several AsyncTasks
 * Holds the response code and the response text.
 */
public class Response {
    private int response_code;
    private String response;

    /**
     * Set default values
     */
    Response() {
        this.response_code = 0;
        this.response = "";
    }

    /**
     * Set the response code
     * @param response_code Response Code
     */
    public void set_response_code(int response_code) {
        this.response_code = response_code;
    }

    /**
     * Set the response directly
     * @param response Response
     */
    public void set_response(String response) {
        this.response = response;
    }

    /**
     * Set the response from the connection InputStream
     * @param is InputSteam returned from connection
     * @throws IOException If response is invalid
     */
    public void set_response(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append('\n');
        }

        this.response = result.toString();
    }

    /**
     * Get the response code
     * @return Response code
     */
    public int get_response_code() {
        return response_code;
    }

    /**
     * Get the response
     * @return Response
     */
    public String get_response() {
        return response;
    }
}
