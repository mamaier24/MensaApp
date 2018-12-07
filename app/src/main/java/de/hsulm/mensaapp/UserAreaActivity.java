package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserAreaActivity extends AppCompatActivity {

    private RatingBar ratingbar;
    private Button button;
    private BroadcastReceiver mReceiver=null;
    private String foodtext;
    private TextView textViewUsername, textViewUserEmail;
    private RecyclerView mRecyclerView;
    private GerichtAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutmanager;
    private  boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //Food dummy = new Food(1, "dummy", "dummy", 0, 0, 0f, "dummy", 0f, R.drawable.ic_android_black);
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
                Toast.makeText(UserAreaActivity.this, "Auf Suche geklickt?", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Abmelden:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;

        }
        return true;
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
            String food_id = getFoodID();
            Intent intent = new Intent(this,DatabaseOperations.class);
            final ArrayList<Food> food_list = new ArrayList<>();
            intent.putExtra("searchQuery", food_id);
            startService(intent);

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

                                Food food = new Food(food_obj.getInt("id"),
                                                     food_obj.getString("name"), food_obj.getString("category"),
                                                     food_obj.getInt("vegan"),
                                                     food_obj.getInt("vegetarian"), food_obj.getLong("price"),
                                                     food_obj.getString("uuid"), 1.0f, R.drawable.ic_restaurant_menu_black_24dp);

                                food_list.add(food);

                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                            mLayoutmanager = new LinearLayoutManager(context);
                            mAdapter = new GerichtAdapter(food_list);
                            mRecyclerView.setLayoutManager(mLayoutmanager);
                            mRecyclerView.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new GerichtAdapter.OnItemClickListener() {
                                @Override
                                public void OnItemClick(int position) {
                                    Intent intent = new Intent(UserAreaActivity.this, GerichtProfil.class);
                                    intent.putExtra("Food", food_list.get(position));
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

    //DB OPERATIONS CREATED BY STEPHAN DANZ
    //END

}

