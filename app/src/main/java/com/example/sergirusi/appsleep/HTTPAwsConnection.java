package com.example.sergirusi.appsleep;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by SergiRusi on 31/03/2016.
 */
public class HTTPAwsConnection extends AsyncTask<JSONObject, Void, Integer> {

    private Context context;

    private URL url;

    private HttpURLConnection connection;

    private ProgressDialog progress;

    private int responseCode;

//    protected void onPreExecute() {
//        progress = new ProgressDialog(context);
//        progress.setMessage("Loading");
//        progress.show();
//    }

    @Override
    protected Integer doInBackground(JSONObject... params) {
        try {
            url = new URL("http://52.50.79.248:8080/create_user");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());

            dStream.write();
            dStream.flush();
            dStream.close();

            responseCode = connection.getResponseCode();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode;
    }

}
