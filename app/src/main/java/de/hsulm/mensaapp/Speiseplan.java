package de.hsulm.mensaapp;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import java.util.*;

import com.github.barteksc.pdfviewer.PDFView;


import de.hsulm.mensaapp.R;

import static de.hsulm.mensaapp.R.id.parent;
import static de.hsulm.mensaapp.R.id.pdfView;

public class Speiseplan extends AppCompatActivity {
    private String link;
    private PDFView pdfview;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;     //Strings den Spinner füllen

    int week = WeekNumber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speiseplan);

        spinner = (Spinner)findViewById(R.id.spinnerStandort);
        adapter = ArrayAdapter.createFromResource(this, R.array.Standorte, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String standort = parent.getItemAtPosition(position).toString();


                switch (standort){  //oder über id
                    case "Prittwitzstraße diese Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/Prittwitzstr" + ((Integer)week).toString() +".pdf";
                        break;
                    case "Prittwitzstraße nächste Woche":
                        week = week+1;
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/Prittwitzstr" + ((Integer)week).toString() +".pdf";
                        break;
                    case "Böfingen diese Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/BÖ" + ((Integer)week).toString() +".pdf";
                        break;
                    case "Böfingen nächste Woche":
                        week = week+1;
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/BÖ" + ((Integer)week).toString() +".pdf";
                        break;
                    case "Eselsberg diese Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/OE" + ((Integer)week).toString() +".pdf";
                        break;
                    case "Eselsberg nächste Woche":
                        week = week+1;
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/OE"  + ((Integer)week).toString() +".pdf";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }
        });

        pdfview =(PDFView)findViewById(R.id.pdfView);
        //Von den Assets
        //pdfview.fromAsset("Speiseplan.pdf").load();

        //Von URL
        String url = link;
        new RetrievePDFStream(url).execute();



    }

    class RetrievePDFStream extends AsyncTask<String,Void,InputStream>
    {
        private String pdfurl;

        public RetrievePDFStream(String url){
            this.pdfurl = url;
        }


        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL test = new URL(pdfurl);
                HttpURLConnection urlConnection =(HttpURLConnection)test.openConnection();
                if(urlConnection.getResponseCode()==200)
                {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            catch (IOException e)
            {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream){
            pdfview.fromStream(inputStream).load();
        }

    }


    public int WeekNumber(){

        String inputDate = "20181201";
        String inputFormat = "yyyyMMdd";

        SimpleDateFormat dateFormat = new SimpleDateFormat(inputFormat);
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);

        return(weekNumber);
        //System.out.println(weekNumber);
    }
}