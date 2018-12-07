package de.hsulm.mensaapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import static de.hsulm.mensaapp.R.layout.activity_gericht_profil;

public class GerichtProfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_gericht_profil);

        Intent intent = getIntent();
        Food exampleItem = intent.getParcelableExtra("Food");

        String Preis = ((Float)exampleItem.getPrice()).toString();
        String Titel = exampleItem.getName();
        String Bewertung = ((Float)exampleItem.getRating()).toString();
        int imageRes = exampleItem.getmimgId();


        TextView mPreis = (TextView) findViewById(R.id.Preis);
        mPreis.setText(Preis);
        TextView mTitel = (TextView) findViewById(R.id.Titel);
        mTitel.setText(Titel);
        TextView mBewertung = (TextView) findViewById(R.id.Bewertung);
        mBewertung.setText(Bewertung);

    }
}
