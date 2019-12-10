package com.example.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncTaskGetAllCategories extends AsyncTask<String, Void, String> {
    private static final String TAG = "ASYNC_CATEGORIES";
    private static final String NEWS_URL_PART1 = "https://newsapi.org/v2/sources?language=en&country=us&category=";
    private static final String NEWS_URL_PART2 = "&apiKey=38419632bd464177b6289c153626ccfc";
    private MainActivity mainActivity;

    public AsyncTaskGetAllCategories(MainActivity mainAct) {mainActivity = mainAct;}

    @Override
    protected void onPostExecute(String s) {
        ArrayList<String> categories = parseJSON(s);
        mainActivity.setRightMenu(categories);

    }

    @Override
    protected String doInBackground(String... params) {
        String category = ""; //empty category returns sources for all categories
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
        return sb.toString();

    }

    public ArrayList<String> parseJSON(String s) {
        ArrayList<String> category_list = new ArrayList<String>();
        try {
            JSONObject return_object = new JSONObject(s);
            JSONArray articles_array = return_object.getJSONArray("sources");
            for (int i=0; i < articles_array.length(); i++) {
                String category = articles_array.getJSONObject(i).getString("category");
                if (!category_list.contains(category))
                    category_list.add(category);
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: ERROR: " + e);
        }
        return category_list;
    }
}
