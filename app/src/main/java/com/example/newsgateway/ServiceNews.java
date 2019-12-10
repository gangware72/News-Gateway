package com.example.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import static com.example.newsgateway.MainActivity.RECEIVER_ARTICLES;

public class ServiceNews extends Service {
    private static final String TAG = "SERVICENEWS";
    public static final String BROADCAST_REQUEST_ARTICLES = "REQUEST ARTICLES";
    public static final String BROADCAST_RETRIEVED_ARTICLES = "RETRIEVED ARTICLES";
    private ReceiverService serviceReceiver;
    public ServiceNews snews =this;
    public ArrayList<Article> articles_array;
    ArrayList<Article> nu_array = new ArrayList<>();
    private boolean isRunning = true;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        
        Runnable r = new Runnable() { //defining a class on the fly
            @Override
            public void run() { //the amin


                while (isRunning) {
                    Log.d(TAG, "run: service is running");
                    if (nu_array.isEmpty()) {
                        try {
                            Thread.sleep(250);
                        } catch (Exception e) {
                            Log.d(TAG, "run: EXCEPTION: " + e.toString());
                        }
                } else {
                    Intent intent = new Intent();
                    intent.setAction(BROADCAST_RETRIEVED_ARTICLES);
                    intent.putExtra("ARTICLES", nu_array);
                    sendBroadcast(intent);
                    nu_array.clear();

                    }
            }
        }};

        Log.d(TAG, "run: REGISTERING");
        serviceReceiver = new ReceiverService();
        IntentFilter articles_filter = new IntentFilter(BROADCAST_REQUEST_ARTICLES);
        registerReceiver(serviceReceiver, articles_filter);
        Thread t = new Thread(r); //actually putting the runnable in a new thread so it can run
        t.start(); //started the thread



        return Service.START_STICKY; //recreate the service if the app goes down
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(serviceReceiver);
        isRunning = false; //stop the new runnable service thread because it will continue after activity and service are shut down
        super.onDestroy();
    }

    /*Broadcasts 10 of the news stories*/
    public void setArticles(ArrayList<Article> articles_list) {
        Log.d(TAG, "setArticles: RETURNED: " + articles_list.toString());
        for (int i=0; i < 10; i++) {
            nu_array.add(articles_list.get(i));
        }

        Log.d(TAG, "setArticles: ARTICLES: " + nu_array.toString());
    }





    public class ReceiverService extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            AsyncTaskGetAllArticles task = new AsyncTaskGetAllArticles(snews);
            Log.d(TAG, "onReceive: RECEIVING");
            String id = intent.getStringExtra("SOURCE_ID");
            task.execute(id);


        }
    }

}
