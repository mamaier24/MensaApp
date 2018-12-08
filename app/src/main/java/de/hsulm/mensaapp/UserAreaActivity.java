package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hsulm.mensaapp.SQL_SEARCH_BY_ID.DatabaseOperations;
import de.hsulm.mensaapp.SQL_SEARCH_BY_ID.IDatabaseOperations;

public class UserAreaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private FoodAdapter mAdapter = null;
    private RecyclerView.LayoutManager mLayoutmanager;
    private SwipeRefreshLayout swipe_refresh;
    private DatabaseOperations operations = new DatabaseOperations(this);
    private ArrayList<FoodClass> food_list2 = new ArrayList<>();
    public final static String extraItem ="food";


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

    }


    public void initializeRecycler() {

        operations.getFoodFromDB(getFoodID(), new IDatabaseOperations() {
            @Override
            public void onSuccess(final ArrayList<FoodClass> food_list) {

                mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mLayoutmanager = new LinearLayoutManager(UserAreaActivity.this);
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

            }
        });

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

                break;

            case R.id.Suche:
                //Intent zum Suchen
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
                initializeRecycler();

                if (swipe_refresh != null) {
                    swipe_refresh.setRefreshing(false);
                }
            }

        }, 4000);

    }


    @Override
    public void onRestart() {
        super.onRestart();
        mAdapter.clear();
        initializeRecycler();
    }


    //DB OPERATIONS CREATED BY STEPHAN DANZ
    //Necessary for DB operations
    //TODO: on resume
    @Override
    public void onPause() {
        super.onPause();
    }


    public String getFoodID() {

        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        int weekNumber = calDe.get(Calendar.WEEK_OF_YEAR);
        int year = calDe.get(Calendar.YEAR);
        String food_id = "Y" + year + ":CW" + weekNumber + ":DMO";
        return (food_id);

    }


}

