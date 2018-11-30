package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SpeiseplanActivity extends AppCompatActivity {

    PDFView pdfview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speiseplan);

         pdfview = (PDFView) findViewById(R.id.pdfView);
        //Von Asstes
        //pdfview.fromAsset("Speiseplan.pdf").load();

        //VOn URL
        new RetrievePDFStream().execute("https://studierendenwerk-ulm.de/wp-content/uploads/speiseplaene/Prittwitzstr48.pdf"); //denn String müssten wir nur auf die aktuelle woche ändern dann passt das :)

    }

    class RetrievePDFStream extends AsyncTask<String,Void,InputStream>
    {
        @Override
        protected InputStream doInBackground(String... strings){
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode() == 200)
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
}

//----Versuch 1---------------------------------------------------------------
 /*pdfview.fromUri(Uri.parse("https://studium.hs-ulm.de/de/Downloads/Speiseplan_EF.pdf"))
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                // allows to draw something on the current page, usually visible in the middle of the screen
                .onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                .onDrawAll(onDrawListener)
                .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
                .onPageChange(onPageChangeListener)
                .onPageScroll(onPageScrollListener)
                .onError(onErrorListener)
                .onPageError(onPageErrorListener)
                .onRender(onRenderListener) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                .onTap(onTapListener)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
                .invalidPageColor(Color.WHITE) // color of page that is invalid and cannot be loaded
                .load();*/

//----Versuch 2---------------------------------------------------------------

   /* String path = Environment.getExternalStorageDirectory().toString();
    File myFile = new File(path + "Speiseplan.pdf");
    downloadFile("https://studium.hs-ulm.de/de/Downloads/Speiseplan_EF.pdf", myFile);


    PDFView pdfview =(PDFView)findViewById(R.id.pdfView);
        pdfview.fromFile(myFile).load();




    private static void downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch(FileNotFoundException e) {
            return; // swallow a 404
        } catch (IOException e) {
            return; // swallow a 404
        }
    }

}


//----Versuch 3---------------------------------------------------------------

/*
            applicationContext = getApplicationContext();

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "pdfDownloads");
            folder.mkdir();
            File file = new File(folder, "Speiseplan.pdf");
            try {
                if(!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            boolean downloadFile = downloadFile("https://studium.hs-ulm.de/de/Downloads/Speiseplan_EF.pdf", file);      //http://www.irs.gov/pub/irs-pdf/fw4.pdf
            if (file!=null && file.exists() && file.length() > 0){
                Intent intent = new Intent(this, com.example.soniapdf.Second.class);
                intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME,
                        file.getAbsolutePath());
                startActivity(intent);
            }
        }

        public static boolean downloadFile(String fileUrl, File directory) {
            try {
                FileOutputStream f = new FileOutputStream(directory);
                URL u = new URL(fileUrl);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                InputStream in = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len = 0;

                //
                int fileLength = c.getContentLength();
                long total = 0;
                //
                Toast.makeText(applicationContext, "Downloading PDF...", 2000).show();


                while ((len = in.read(buffer)) > 0) {
                    total += len;


                    //Toast.makeText(applicationContext, "Downloading PDF: remaining " + (fileLength / total  )+ "%", 1).show();
                    f.write(buffer, 0, len);
                }
                f.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }



 */