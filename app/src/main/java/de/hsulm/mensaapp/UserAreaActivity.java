package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

public class UserAreaActivity extends AppCompatActivity {

    RatingBar ratingbar;
    Button button;
    BroadcastReceiver mReceiver;

    private TextView textViewUsername, textViewUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUseremail);

        addListenerOnButtonClick();

        textViewUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());

        //Essential for reception of food from DB
        Intent intent = new Intent(this, DatabaseOperations.class);
        intent.putExtra("searchQuery","2018KW49F1");
        startService(intent);

         mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                JSONArray food_arr;
                JSONObject food_obj;
                ArrayList<Food> food_list = new ArrayList<>();

                try {
                    food_arr = new JSONArray(intent.getStringExtra("food_object"));

                     for(int i=0; i<food_arr.length();i++) {

                         food_obj = food_arr.getJSONObject(i);
                         Food food = new Food(food_obj.getInt("id"), food_obj.getString("name"), food_obj.getString("category"), food_obj.getString("date"),food_obj.getInt("vegan"),food_obj.getInt("vegetarian"),food_obj.getLong("price"),food_obj.getString("uuid"));
                         food_list.add(food);
                     }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("Success!");

            }
        };

        registerReceiver(mReceiver, new IntentFilter(DatabaseOperations.CUSTOM_INTENT));
        //END of DB operations

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void addListenerOnButtonClick(){
        ratingbar=(RatingBar)findViewById(R.id.ratingBar);
        button=(Button)findViewById(R.id.button);
        //Performing action on Button Click
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                //Getting the rating and displaying it on the toast
                String rating=String.valueOf(ratingbar.getRating());
                Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
            }

        });
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Speiseplan:
                //Intent zum speiseplan
                Intent speiseplanIntent = new Intent(UserAreaActivity.this, MenuActivity.class);
                UserAreaActivity.this.startActivity(speiseplanIntent);
                Toast.makeText(this, "Auf MenuActivity geklickt?", Toast.LENGTH_LONG).show();
                break;

            case R.id.Lob_Tadel:
                //Intent zu Lob und Tadel
                Log.i("Want to Send Mail","");
                String[] TO = {"mensa@studierendenwerk-ulm.de"};//Email
                String[] CC = new String[] {SharedPrefManager.getInstance(this).getUserEmail()};
                String UN = SharedPrefManager.getInstance(this).getUsername();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bitte geben sie Ihre Speise(n) an:\n");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hier ist Platz für Lob und konstruktive Kritik:\n\n\n\nViele Grüße " +UN);


                try{
                    startActivity(Intent.createChooser(emailIntent, "Wählen Sie eine Email-App aus..."));
                    finish();
                    Log.i("Mail Sent", "");
                }catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(this,"Mail is not initiated", Toast.LENGTH_SHORT).show();
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


    //Necessary for DB operations
    //TODO: on resume
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}

