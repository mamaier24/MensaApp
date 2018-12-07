package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hsulm.mensaapp.SQL_SEARCH_BY_ID.DatabaseOperations;

public class UserAreaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RatingBar ratingbar;
    private Button button;
    private BroadcastReceiver mReceiver=null;
    private String foodtext;
    private TextView textViewUsername, textViewUserEmail;
    private RecyclerView mRecyclerView;
    private FoodAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutmanager;
    private  boolean flag=false;
    private SwipeRefreshLayout swipe_refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        swipe_refresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);

        initializeRecycler();

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //FoodClass dummy = new FoodClass(1, "dummy", "dummy", 0, 0, 0f, "dummy", 0f, R.drawable.ic_android_black);
        //food_list.add(dummy);
        getFoodFromDB();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Speiseplan:
                //Intent for "Speiseplan"
                Intent speiseplanIntent = new Intent(UserAreaActivity.this, MenuActivity.class);
                UserAreaActivity.this.startActivity(speiseplanIntent);
                break;

            case R.id.Lob_Tadel:
                //Intent for "Lob und Tadel"
                Log.i("Want to Send Mail", "");
                String[] TO = {"mensa@studierendenwerk-ulm.de"};//Email
                String[] CC = new String[]{SharedPrefManager.getInstance(this).getUserEmail()};
                String UN = SharedPrefManager.getInstance(this).getUsername();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bitte geben sie Ihre Speise(n) an:\n");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hier ist Platz für Lob und konstruktive Kritik:\n\n\n\nViele Grüße " + UN);


                try {
                    startActivity(Intent.createChooser(emailIntent, "Wählen Sie eine Email-App aus..."));
                    finish();
                    Log.i("Mail gesendet", "");
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Mail is not initiated", Toast.LENGTH_SHORT).show();
                }


                //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Noch nicht bearbeitet
                Toast.makeText(UserAreaActivity.this, "Auf Lob und Tadel geklickt?", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Suche:
                //Intent zum Suche
                Intent sucheIntent = new Intent(UserAreaActivity.this, SearchActivity.class);
                UserAreaActivity.this.startActivity(sucheIntent);
                break;

            case R.id.Abmelden:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;

        }
        return true;
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override public void run() {
                mAdapter.clear();
                String food_id = getFoodID();
                Intent intent = new Intent(UserAreaActivity.this,DatabaseOperations.class);
                intent.putExtra("searchQuery", food_id);
                startService(intent);

                if (swipe_refresh != null) {
                    swipe_refresh.setRefreshing(false);
                }

            }
        }, 4000);

    }


    @Override
    public void onRestart() {
        super.onRestart();
        registerReceiver(mReceiver, new IntentFilter(DatabaseOperations.CUSTOM_INTENT));
        mAdapter.clear();
        String food_id = getFoodID();
        Intent intent = new Intent(UserAreaActivity.this,DatabaseOperations.class);
        intent.putExtra("searchQuery", food_id);
        startService(intent);
    }


    //DB OPERATIONS CREATED BY STEPHAN DANZ
    //Necessary for DB operations
    //TODO: on resume
    @Override
    public void onPause() {
        super.onPause();
        try {
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        }catch (IllegalArgumentException e){
            mReceiver=null;
        }
    }


    public String getFoodID() {

        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        int weekNumber = calDe.get(Calendar.WEEK_OF_YEAR);
        int year = calDe.get(Calendar.YEAR);
        String food_id = "Y" + year + ":CW" + weekNumber + ":DMO";
        return (food_id);

    }


    public ArrayList getFoodFromDB() {

            //DB OPERATIONS CREATED BY STEPHAN DANZ
            //Essential for reception of food from DB

            final ArrayList<FoodClass> food_list = new ArrayList<>();

            if(mReceiver==null) {

                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        JSONArray food_arr;
                        JSONObject food_obj;

                        try {
                            food_arr = new JSONArray(intent.getStringExtra("food_object"));

                            for (int i = 0; i < food_arr.length(); i++) {
                                food_obj = food_arr.getJSONObject(i);

                                FoodClass food = new FoodClass(food_obj.getInt("id"),
                                                     food_obj.getString("name"), food_obj.getString("category"),
                                                     food_obj.getInt("vegan"),
                                                     food_obj.getInt("vegetarian"), food_obj.getString("price"),
                                                     food_obj.getString("uuid"), 1.0f, "MA_PIC_ID_A1XX.png");

                                food_list.add(food);

                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                            mLayoutmanager = new LinearLayoutManager(context);
                            mAdapter = new FoodAdapter(food_list);
                            mRecyclerView.setLayoutManager(mLayoutmanager);
                            mRecyclerView.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
                                @Override
                                public void OnItemClick(int position) {
                                    Intent intent = new Intent(UserAreaActivity.this, FoodProfile.class);
                                    intent.putExtra("food", food_list.get(position));
                                    startActivity(intent);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                registerReceiver(mReceiver, new IntentFilter(DatabaseOperations.CUSTOM_INTENT));
                //END of DB operations
                //DB OPERATIONS CREATED BY STEPHAN DANZ

            }

            return food_list;

    }

    public void initializeRecycler(){

        getFoodFromDB();

        String food_id = getFoodID();
        Intent intent = new Intent(this,DatabaseOperations.class);
        intent.putExtra("searchQuery", food_id);
        startService(intent);

    }

    //DB OPERATIONS CREATED BY STEPHAN DANZ
    //END

}

