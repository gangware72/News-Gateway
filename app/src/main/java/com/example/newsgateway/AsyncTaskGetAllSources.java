package com.example.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AsyncTaskGetAllSources extends AsyncTask<String, Void, String> {

    private static final String NEWS_URL_PART1 = "https://newsapi.org/v2/sources?language=en&country=us&category=";
    private static final String NEWS_URL_PART2 = "&apiKey=38419632bd464177b6289c153626ccfc";
    private static final String TAG = "AsyncTaskGetAllSources";
    private MainActivity mainActivity; //change to whatever activity launches async later

    public AsyncTaskGetAllSources(MainActivity mainAct) {mainActivity = mainAct;}

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> source_map = parseJSON(s, "sources");
        HashMap<String, String> category = parseJSON(s, "category");
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, String> entry: source_map.entrySet())
            sb.append(entry.getKey() + ", " + entry.getValue() +":");

        Log.d(TAG, "onPostExecute: SOURCES" + sb.toString());
        mainActivity.getCategories(source_map, category);


    }

    @Override
    protected String doInBackground(String... params) {
        //String category = params[0].trim(); TURN ON LATER
        String category = params[0].trim(); //empty category returns sources for all categories
        String NEWS_URL = NEWS_URL_PART1 + category.trim() + NEWS_URL_PART2;
        Uri dataUri = Uri.parse(NEWS_URL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder(); //used for building return string

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (Exception e) {
            Log.d(TAG, "doInBackground: EXCEPTION" + e);
            return null;
        }
        Log.d(TAG, "doInBackground: Categories Return Query: " + sb.toString());
        return sb.toString();


    }

    public HashMap<String, String> parseJSON(String s, String type) {
        HashMap<String, String> source_list = new HashMap<>();
        HashMap<String, String> category_list = new HashMap<>();
        try {
            JSONObject return_object = new JSONObject(s);
            JSONArray articles_array = return_object.getJSONArray("sources");
            for (int i=0; i < articles_array.length(); i++) {
                String id = articles_array.getJSONObject(i).getString("id");
                String name = articles_array.getJSONObject(i).getString("name");
                String category = articles_array.getJSONObject(i).getString("category");
                source_list.put(name.trim(),id.trim());
                category_list.put(name.trim(), category.trim());

            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: ERROR: " + e);
        }

        if (type.equals("sources"))
            return source_list;
        return category_list;

    }
}
