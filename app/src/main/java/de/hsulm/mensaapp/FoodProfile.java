package de.hsulm.mensaapp;

import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;

import java.io.InputStream;

import static de.hsulm.mensaapp.R.layout.activity_food_profile;
import static de.hsulm.mensaapp.R.layout.indicator_layout;

public class FoodProfile extends AppCompatActivity {
    private SliderLayout sliderShow;
    private DefaultSliderView defaultSliderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_food_profile);

        TextView mBewertung = (TextView) findViewById(R.id.Bewertung);
        TextView mTitel = (TextView) findViewById(R.id.Titel);
        TextView mPreis = (TextView) findViewById(R.id.Preis);
        RatingBar mRatingBar = (RatingBar)findViewById(R.id.ratingBar2);
        sliderShow = (SliderLayout)findViewById(R.id.imageSlider);
        defaultSliderView = new DefaultSliderView(this);

        FoodClass food = getIntent().getParcelableExtra("food");

        String price = food.getPrice();
        String name = food.getName();
        String rating = ((Integer)food.getRating()).toString();
        String imageRes = food.getmimgId();


        new DownloadProfileImage().execute(Constants.ROOT_URL_PICTURES + imageRes);

        mRatingBar.setRating(Float.parseFloat(rating));

        mPreis.setText(price);

        mTitel.setText(name);

        mBewertung.setText("Ø " + rating);

    }


    private class DownloadProfileImage extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls) {

            try {
                if (urls.length <2 ){
                    defaultSliderView
                            .image(urls[0])
                            .setScaleType(BaseSliderView.ScaleType.Fit);
                    sliderShow.addSlider(defaultSliderView);
                    sliderShow.stopAutoCycle();
                    sliderShow.setPagerTransformer(false, new BaseTransformer() {
                        @Override
                        protected void onTransform(View view, float position) {
                        }
                    });
                }
                else {
                    for (int i = 0; i<urls.length;i++){
                        defaultSliderView
                                .image(urls[i])
                                .setScaleType(BaseSliderView.ScaleType.Fit);
                        sliderShow.addSlider(defaultSliderView);
                        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                    }
                }

            } catch (Exception e) {
                defaultSliderView
                        .image(R.drawable.ic_restaurant_menu_black_24dp)
                        .setScaleType(BaseSliderView.ScaleType.Fit);
                sliderShow.addSlider(defaultSliderView);
                Log.d("Error","Image Download failed");
            }

            sliderShow.setDuration(7500);
            return null;
        }


    }

}
