package de.hsulm.mensaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static de.hsulm.mensaapp.R.layout.activity_gericht_profil;

public class FoodProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_gericht_profil);

        Intent intent = getIntent();
        FoodClass exampleItem = intent.getParcelableExtra("food");

        String price = exampleItem.getPrice();
        String name = exampleItem.getName();
        String rating = ((Float)exampleItem.getRating()).toString();
        String imageRes = exampleItem.getmimgId();


        TextView mPreis = (TextView) findViewById(R.id.Preis);
        mPreis.setText(price);
        TextView mTitel = (TextView) findViewById(R.id.Titel);
        mTitel.setText(name);
        TextView mBewertung = (TextView) findViewById(R.id.Bewertung);
        mBewertung.setText(rating);

    }
}
