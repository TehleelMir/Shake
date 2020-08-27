package com.example.shake;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class loaderClass extends AsyncTaskLoader<DateType> {
    double mag;

    public loaderClass(@NonNull Context context, double mag) {
        super(context);
        this.mag = mag;
    }

    @Nullable
    @Override
    public DateType loadInBackground() {
        return fetchData();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    private DateType fetchData() {
        String temp = "&minmagnitude=" + mag;
        String urlString = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&limit=1";
        if (mag != -1.0)
            urlString += temp;
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e("loderClass", e + "");
        }
        String josonReponce = getJson(url);
        Log.e("loader Class", "hereee we areeeeeeeeeeeeeeeeee ");
        return convertTheJson(josonReponce);
    }

    private String getJson(URL url) {
        String json = "";
        if (url == null) return json;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                json = getDataFromInputStream(inputStream);
            } else
                Log.e("loaderClass", "something is wront with responce coee");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) httpURLConnection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return json;
    }

    private String getDataFromInputStream(InputStream inputStream) {
        StringBuilder json = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader reder = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reder);
            try {
                String line = bufferedReader.readLine();
                while (line != null) {
                    json.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json.toString();
    }

    private DateType convertTheJson(String json) {
        if (json.isEmpty()) return null;
        try {
            JSONObject root = new JSONObject(json);
            JSONArray rootArray = root.getJSONArray("features");
            JSONObject firstIndex = rootArray.getJSONObject(0);
            JSONObject properties = firstIndex.getJSONObject("properties");
            double mag = properties.getDouble("mag");
            long time = properties.getLong("time");
            String place = properties.getString("place");
            return new DateType(mag, place, time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
