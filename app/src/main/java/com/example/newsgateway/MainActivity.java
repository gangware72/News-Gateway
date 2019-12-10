package com.example.newsgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import static android.media.CamcorderProfile.get;

public class MainActivity extends AppCompatActivity {

    private boolean serviceRunning = false;
    private String ACTION_MSG_TO_SERVICE = "message";
    private Menu menu;
    private ArrayList<String> categories;
    private static final int MENU_A = 100;
    private static final int GROUP_A = 10;
    private DrawerLayout drawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle; //this is the hamburger or three bars on top left.
    private ArrayList<DrawerObject> drawer_items = new ArrayList<>(); //will convert has map with id's to this
    private HashMap<String, String> drawer_map; //will be used to get ids for article searches
    private ColorArrayAdapter drawerAdapter;
    public static final String BROADCAST_REQUEST_ARTICLES = "REQUEST ARTICLES";
    public static final String BROADCAST_RETRIEVED_ARTICLES = "RETRIEVED ARTICLES";
    public static final String BROADCAST_REQUEST_ARTILCLES_SOURCE_DATA = "";
    public static final String RECEIVER_ARTICLES = "RECEIVE ARTICLES";
    private ReceiverNews newsReceiver;
    private static final String TAG = "MainActivity";
    private List<Fragment> fragments = new ArrayList<>();
    private NewsPagerAdapter newsAdapter;
    private ViewPager mViewPager;
    private HashMap<String, Integer> color_map = new HashMap<>();
    private ArrayList<Integer> available_colors = new ArrayList<Integer>(Arrays.asList(Color.RED,
            Color.GREEN, Color.MAGENTA, Color.GRAY, Color.YELLOW, Color.BLUE, Color.rgb(255, 153, 51),
            Color.CYAN, Color.rgb(255,51, 153)));
    private ImageView image;
    private ArrayList<Article> currentlyDisplayedArticles = new ArrayList<>();
    private boolean receiverIsRegistered = true;
    private View fragment_view;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override //for toggling
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean isConnected = doNetCheck();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        drawerAdapter = new ColorArrayAdapter(this,
                R.layout.drawer_list_items, drawer_items);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectItem(i);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open_Drawer, R.string.Close_Drawer);

        if (!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Establish Connection Before Launching. Now Closing News Gateway.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    receiverIsRegistered = false;
                    finish();

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            //Register the receiver
            newsReceiver = new ReceiverNews();
            IntentFilter articles_filter = new IntentFilter(BROADCAST_RETRIEVED_ARTICLES);
            registerReceiver(newsReceiver, articles_filter);

            //set Drawer List to empty and initialize


            //get categories
            new AsyncTaskGetAllCategories(this).execute();

            newsAdapter = new NewsPagerAdapter(getSupportFragmentManager());
            mViewPager = findViewById(R.id.viewPager);
            mViewPager.setAdapter(newsAdapter);

            image = findViewById(R.id.image);
            fragment_view = findViewById(R.id.fragment_constraint);





            /*Starting the News Service*/
            Intent intent = new Intent(MainActivity.this, ServiceNews.class);
            //intent.putExtra("IntentFilter",ACTION_MSG_TO_SERVICE); PASS ANY DATA
            startService(intent);
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }
        drawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        drawerAdapter = new ColorArrayAdapter(this,
                R.layout.drawer_list_items, drawer_items);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectItem(i);
                    }
                }
        );
        mDrawerToggle.syncState();

    }

    @Override
    protected void onStop() { //kills the news service when the app stops, could do onDestroy but do not know when that will occur
        Intent intent = new Intent(MainActivity.this,ServiceNews.class);
        stopService(intent);
        super.onStop();
    }

    public void setArticles () {
        //set Articles
        //clear Article list
        //Fill the article list using the content of the list passed in
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //need to check if someone was using drawerlayout or initial menu
        if (mDrawerToggle.onOptionsItemSelected(item)) { //toggle selected but deal with it up there
        return true;
        } else { //toggle not selected menu selected
            String category = item.getTitle().toString().trim();
            if (category == "all") category = "";
            new AsyncTaskGetAllSources(this).execute(category);

        }
        return super.onOptionsItemSelected(item);
    }

    public void setRightMenu(ArrayList<String> cat) {
        MenuItem menuItem;
        menu.clear();
        menu.add(GROUP_A,MENU_A, 0, "all");
        for (String category: cat) {
            Integer item_color = available_colors.get(0);
            Log.d(TAG, "setRightMenu: CATEGORY" + category);
            Log.d(TAG, "setRightMenu: COLOR" + Integer.toString(item_color));
            color_map.put(category, item_color);
            available_colors.remove(item_color);
            menuItem = menu.add(GROUP_A, MENU_A, 0, category);
            SpannableString s = new SpannableString(category);
            s.setSpan(new ForegroundColorSpan(item_color), 0, s.length(),0);
            menuItem.setTitle(s);
        }




        categories = cat;
    }

    public void selectItem(int i) {

        String id = drawer_map.get(drawer_items.get(i).getText());
        Intent intent = new Intent();
        intent.setAction(MainActivity.BROADCAST_REQUEST_ARTICLES);
        intent.putExtra("SOURCE_ID", id);
        sendBroadcast(intent);

    }

    public void getCategories(HashMap<String, String> sources, HashMap<String, String> categories) {
        drawer_items.clear();
        drawer_map = sources;

        for (HashMap.Entry<String, String> entry: sources.entrySet()) {
            DrawerObject d_object = new DrawerObject(entry.getKey(),color_map.get(categories.get(entry.getKey())));
            drawer_items.add(d_object);

        }
        drawerAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onDestroy() {
        if (receiverIsRegistered)
            unregisterReceiver(newsReceiver);
        super.onDestroy();
    }
    
    
    
    /////////////////////////////////////////////////////////////
    class ReceiverNews extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            ArrayList<Article> array_list = (ArrayList<Article>) intent.getSerializableExtra("ARTICLES");
            currentlyDisplayedArticles = array_list;
            Log.d(TAG, "onReceive: ARTICLES" + array_list.toString());

            for (int i=0; i< array_list.size(); i++) {
                newsAdapter.notifyChangeInPosition(i);
            }

            fragments.clear();

            for (int i=0; i<array_list.size();i++) {
                array_list.get(i).setPosition(i);
                fragments.add(NewsFragment.newInstance( array_list.get(i)));
            }

            newsAdapter.notifyDataSetChanged();

            mViewPager.setCurrentItem(0);
            image.setImageResource(android.R.color.transparent);
            
        }
    }

    public void goToArticle(View v) {
        Integer position = mViewPager.getCurrentItem();
        Log.d(TAG, "goToArticle: position: " + position);
        Fragment fragment = newsAdapter.getItem(position);
        Log.d(TAG, "goToArticle: ");
        Bundle bndl = fragment.getArguments();
        String url = bndl.getString("URL").trim();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //FragmentManager fm = getSupportFragmentManager();
        //List<Fragment> fragment_list = fm.getFragments();
        outState.putSerializable("FRAGMENT_LIST", (Serializable) fragments);
        outState.putSerializable("DRAWER_LIST", (Serializable) drawer_items);
        outState.putSerializable("DRAWER_MAP", (Serializable) drawer_map);
        //HERE
//        outState.pu
//        outState.putString("DRAWER", drawerLayout.getText());
//        outState.putString("VIEWPAGER", mViewPager.getText());
//        outState.putString("PRIMARY_BACKGROUND", image.setImageResource(android.R.color.transparent));
        super.onSaveInstanceState(outState);
    }



    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            fragments = (ArrayList<Fragment>) savedInstanceState.getSerializable("FRAGMENT_LIST");
            newsAdapter.notifyDataSetChanged();
            drawer_items = (ArrayList<DrawerObject>) savedInstanceState.getSerializable("DRAWER_LIST");
            drawerAdapter.notifyDataSetChanged();
            drawer_map = (HashMap<String, String>) savedInstanceState.getSerializable("DRAWER_MAP");
        }




        //HERE
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
           return false;
        }
    }

    /////////////////////////////////////////////////////////////
    class NewsPagerAdapter extends FragmentPagerAdapter {
        private long baseId = 0;
        NewsPagerAdapter(FragmentManager fm) {super(fm);}

        @Override
        public int getItemPosition(@NonNull Object object) {

            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }


}
