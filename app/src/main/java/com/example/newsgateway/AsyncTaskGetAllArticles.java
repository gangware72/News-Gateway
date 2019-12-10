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

public class AsyncTaskGetAllArticles extends AsyncTask<String, Void, String> {

    private static final String NEWS_URL_PART1 = "https://newsapi.org/v2/everything?sources=";
    private static final String NEWS_URL_PART2 = "&language=en&pageSize=100&apiKey=38419632bd464177b6289c153626ccfc";
    private static final String TAG = "AsyncTaskGetAllArticles";
    public ServiceNews serviceNews;
    private String id;

    public AsyncTaskGetAllArticles(ServiceNews snews) {serviceNews = snews;}


    @Override
    protected void onPostExecute(String s) {
        ArrayList<Article> article_list = parseJSON(s);
        Log.d(TAG, "onPostExecute: FINISH: " + article_list.toString());
        serviceNews.setArticles(article_list);

    }

    /*
    This needs to be given the id, not the name--else it won't work
     */
    @Override
    protected String doInBackground(String... s) {
        //String source = s[0].trim(); //REPLACE
        String source = s[0];
        String NEWS_URL = NEWS_URL_PART1 + source.trim() + NEWS_URL_PART2;
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

    public ArrayList<Article> parseJSON(String s) {
        ArrayList<Article> article_list = new ArrayList<Article>();
        try {
            JSONObject return_object = new JSONObject(s);
            JSONArray articles_array = return_object.getJSONArray("articles");
            for (int i=0; i < articles_array.length(); i++) {
                Article article = new Article(articles_array.getJSONObject(i).getString("author").toString(),
                        articles_array.getJSONObject(i).getString("title").trim(),
                        articles_array.getJSONObject(i).getString("description".trim()),
                        articles_array.getJSONObject(i).getString("url").trim(),
                        articles_array.getJSONObject(i).getString("urlToImage").trim(),
                        articles_array.getJSONObject(i).getString("publishedAt").trim());
                article_list.add(article);
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: ERROR: " + e);
        }
        return article_list;
    }
}
