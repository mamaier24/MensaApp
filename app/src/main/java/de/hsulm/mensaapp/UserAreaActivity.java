package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class UserAreaActivity extends AppCompatActivity {


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


        textViewUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Speiseplan:
                //Intent zum speiseplan
                Intent speiseplanIntent = new Intent(UserAreaActivity.this, SpeiseplanActivity.class);
                UserAreaActivity.this.startActivity(speiseplanIntent);
                Toast.makeText(this, "Auf Speiseplan geklickt?", Toast.LENGTH_LONG).show();
                break;

            case R.id.Lob_Tadel:
                //Intent zu Lob und Tadel
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
}
