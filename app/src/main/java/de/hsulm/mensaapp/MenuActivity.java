package de.hsulm.mensaapp;

import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.*;

import com.github.barteksc.pdfviewer.PDFView;

public class MenuActivity extends AppCompatActivity {
    private String link;
    private PDFView pdfview;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;     //Strings den Spinner füllen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        spinner = (Spinner)findViewById(R.id.spinnerStandort);
        pdfview =(PDFView)findViewById(R.id.pdfView);
        adapter = ArrayAdapter.createFromResource(this, R.array.Standorte, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String location = parent.getItemAtPosition(position).toString();


                switch (location){
                    case "Prittwitzstraße diese Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/Prittwitzstr" + getWeekNumber() +".pdf";
                        new RetrievePDFStream(link).execute();
                        break;
                    case "Prittwitzstraße nächste Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/Prittwitzstr" + (getWeekNumber() + 1) +".pdf";
                        new RetrievePDFStream(link).execute();
                        break;
                    case "Böfingen diese Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/BÖ" + getWeekNumber() +".pdf";
                        new RetrievePDFStream(link).execute();
                        break;
                    case "Böfingen nächste Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/BÖ" + (getWeekNumber() + 1) +".pdf";
                        new RetrievePDFStream(link).execute();
                        break;
                    case "Eselsberg diese Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/OE" + getWeekNumber() +".pdf";
                        new RetrievePDFStream(link).execute();
                        break;
                    case "Eselsberg nächste Woche":
                        link = "https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/OE"  + (getWeekNumber() + 1) +".pdf";
                        new RetrievePDFStream(link).execute();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }

        });

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
                return inputStream = null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream){
            if(inputStream != null){
                pdfview.fromStream(inputStream).load();
            }
            else{
                Toast.makeText(MenuActivity.this, "Speiseplan nicht verfügbar!", Toast.LENGTH_LONG).show();
            }
        }

    }


    public int getWeekNumber(){

        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        int weekNumber = calDe.get(Calendar.WEEK_OF_YEAR);

        return(weekNumber);
    }

}