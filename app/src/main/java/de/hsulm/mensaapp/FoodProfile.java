package de.hsulm.mensaapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;

import static de.hsulm.mensaapp.R.layout.activity_gericht_profil;

public class FoodProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_gericht_profil);

        TextView mBewertung = (TextView) findViewById(R.id.Bewertung);
        TextView mTitel = (TextView) findViewById(R.id.Titel);
        TextView mPreis = (TextView) findViewById(R.id.Preis);
        ImageView mImage = (ImageView) findViewById(R.id.Bild) ;
        RatingBar mRatingBar = (RatingBar)findViewById(R.id.ratingBar2);

        FoodClass food = getIntent().getParcelableExtra("food");

        String price = food.getPrice();
        String name = food.getName();
        String rating = ((Integer)food.getRating()).toString();
        String imageRes = food.getmimgId();


        new DownloadProfileImage(mImage).execute(Constants.ROOT_URL_PICTURES + imageRes);

        mRatingBar.setRating(Float.parseFloat(rating));

        mPreis.setText(price);

        mTitel.setText(name);

        mBewertung.setText("Ø " + rating);

    }


    private class DownloadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

}
