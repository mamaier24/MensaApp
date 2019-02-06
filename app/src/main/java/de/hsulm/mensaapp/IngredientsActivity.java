package de.hsulm.mensaapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.github.barteksc.pdfviewer.PDFView;

/**
 * Created by Marcel Maier on 30/11/18.
 */
public class IngredientsActivity extends AppCompatActivity {

    private String link;
    private PDFView pdfview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        pdfview =(PDFView)findViewById(R.id.pdfView);
        link = "https://studierendenwerk-ulm.de/wp-content/uploads/2016/12/Allergenaushang-A4-quer.pdf";
        new RetrievePDFStream(link).execute();
    }

    class RetrievePDFStream extends AsyncTask<String,Void,InputStream> {
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
            } catch (IOException e) {
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
                Toast.makeText(IngredientsActivity.this, "Inhaltsstoffe nicht verfügbar!", Toast.LENGTH_LONG).show();
            }
        }
    }

}