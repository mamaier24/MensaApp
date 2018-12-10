package de.hsulm.mensaapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;

import de.hsulm.mensaapp.SQL_FETCH_OR_CREATE_RATING.DatabaseOperationsFetch;
import de.hsulm.mensaapp.SQL_FETCH_OR_CREATE_RATING.DatabaseOperationsRatingSet;

import static de.hsulm.mensaapp.R.layout.activity_food_profile;

public class FoodProfile extends AppCompatActivity {

    private FoodClass food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_food_profile);

        TextView mBewertung = (TextView) findViewById(R.id.Bewertung);
        TextView mTitel = (TextView) findViewById(R.id.Titel);
        TextView mPreis = (TextView) findViewById(R.id.Preis);
        ImageView mImage = (ImageView) findViewById(R.id.Bild);
        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar2);

        food = getIntent().getParcelableExtra("food");
        int user_id = SharedPrefManager.getInstance(FoodProfile.this).getUserId();
        int food_id = food.getId();


        String price = food.getPrice();
        String name = food.getName();
        //String rating = ((Integer) food.getRating()).toString();
        String imageRes = food.getmimgId();

        DatabaseOperationsFetch get_rating = new DatabaseOperationsFetch(FoodProfile.this);
        int fetched_rating = get_rating.setAndGetRating(user_id, food_id);

        if(fetched_rating != 0) {
            mRatingBar.setRating(fetched_rating);
        }

        new DownloadProfileImage(mImage).execute(Constants.ROOT_URL_PICTURES + imageRes);

        //mRatingBar.setRating(Float.parseFloat(rating));

        mPreis.setText(price);

        mTitel.setText(name);

        //mBewertung.setText("Ø " + rating);

        mRatingBar.setStepSize(1);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar mRatingBar, float user_rating, boolean fromUser) {
                int user_id = SharedPrefManager.getInstance(FoodProfile.this).getUserId();
                int food_id = food.getId();
                DatabaseOperationsRatingSet new_rating = new DatabaseOperationsRatingSet(FoodProfile.this);
                new_rating.setAndGetRating(user_id, food_id, Math.round(user_rating));
            }


        });

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
                mIcon11 = null;
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                bmImage.setImageBitmap(result);
                bmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

}
