package com.example.sergirusi.appsleep.Connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by SergiRusi on 31/03/2016.
 */
public class HttpPostConnection extends AsyncTask<String, String, String> {

    private static final String TAG = "RESPONSE";

    private Context context;

    private URL url;

    private HttpURLConnection connection;

    private ProgressDialog progress;

    private int responseCode;

    @Override
    protected String doInBackground(String... params) {
        String jsonResponse = null;
        String jsonData = params[0];
        String cameraData = params[1];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = null;
            if (cameraData == null) {
                url = new URL("http://52.50.79.248:8080/create_user");
            } else {
                if(cameraData.equals("ON")) {
                    url = new URL("http://52.50.79.248:8080/camon");
                } else {
                    url = new URL("http://52.50.79.248:8080/camoff");
                }
            }
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(jsonData);
            writer.close();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            jsonResponse = buffer.toString();
//response data
            Log.i(TAG, jsonResponse);
            //send to post execute
            return jsonResponse;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

//    protected void onPreExecute() {
//        progress = new ProgressDialog(context);
//        progress.setMessage("Loading");
//        progress.show();
//    }


}
