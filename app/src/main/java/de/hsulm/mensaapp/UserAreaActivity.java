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
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserAreaActivity extends AppCompatActivity {

    private RatingBar ratingbar;
    private Button button;
    private BroadcastReceiver mReceiver=null;
    private String foodtext;
    private TextView textViewUsername, textViewUserEmail;
    private ArrayList<Food> global_food_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUseremail);

        addListenerOnButtonClick();
        addRefreshButtonListener();


        textViewUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    public void addListenerOnButtonClick() {
        ratingbar = (RatingBar) findViewById(R.id.ratingBar);
        button = (Button) findViewById(R.id.button);
        //Performing action on Button Click
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Getting the rating and displaying it on the toast
                String rating = String.valueOf(ratingbar.getRating());
                Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
            }

        });
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
                //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Noch nicht bearbeitet
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
        unregisterReceiver(mReceiver);
    }


    public String getFoodID() {

        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        int weekNumber = calDe.get(Calendar.WEEK_OF_YEAR);
        int year = calDe.get(Calendar.YEAR);
        String food_id = "Y" + year + ":CW" + weekNumber + ":DMO";
        return (food_id);

    }


    public void addRefreshButtonListener() {

        Button refreshButton = (Button) findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v) {

                //DB OPERATIONS CREATED BY STEPHAN DANZ
                //Essential for reception of food from DB
                final TextView textView = (TextView) findViewById(R.id.textView2);
                textView.setMovementMethod(new ScrollingMovementMethod());
                String food_id = getFoodID();
                foodtext = "";
                Intent intent = new Intent(v.getContext(),DatabaseOperations.class);
                intent.putExtra("searchQuery", food_id);
                startService(intent);

                if(mReceiver==null) {

                    mReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            JSONArray food_arr;
                            JSONObject food_obj;
                            ArrayList<Food> food_list = new ArrayList<>();

                            try {
                                food_arr = new JSONArray(intent.getStringExtra("food_object"));

                                for (int i = 0; i < food_arr.length(); i++) {
                                    food_obj = food_arr.getJSONObject(i);
                                    Food food = new Food(food_obj.getInt("id"), food_obj.getString("name"), food_obj.getString("category"), food_obj.getString("date"), food_obj.getInt("vegan"), food_obj.getInt("vegetarian"), food_obj.getLong("price"), food_obj.getString("uuid"));
                                    foodtext = foodtext + food.getName() + "\n";
                                    textView.setText(foodtext);
                                    food_list.add(food);
                                }

                                distributeFoodList(food_list);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    registerReceiver(mReceiver, new IntentFilter(DatabaseOperations.CUSTOM_INTENT));
                    //END of DB operations
                    //DB OPERATIONS CREATED BY STEPHAN DANZ

                }
            }
        });

        refreshButton.performClick();

    }

    public synchronized void distributeFoodList(ArrayList food_list){
        global_food_list = food_list;
        System.out.println(global_food_list);
    }
    //DB OPERATIONS CREATED BY STEPHAN DANZ
    //END

}

