package de.hsulm.mensaapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.DatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.DatabaseOperationsSetRating;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.IDatabaseOperationsFetchRating;

import static de.hsulm.mensaapp.R.layout.activity_food_profile;

public class FoodProfile extends AppCompatActivity {

    private SliderLayout sliderShow;
    private DefaultSliderView defaultSliderView;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 0;
    InputStream inputStream;
    Bitmap image;
    ImageView placeholder;
    ByteArrayOutputStream byteArrayOutputStream;
    ProgressDialog progressDialog;
    String imageTag = "image_tag";
    String imageName = "image_data";
    URL url;
    HttpURLConnection httpURLConnection;
    OutputStream outputStream;
    String root_url = "http://www.s673993392.online.de/pictures/a";
    private FoodClass food;

    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;     //Strings den Spinner füllen
    String Standort=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_food_profile);

        int user_id = SharedPrefManager.getInstance(FoodProfile.this).getUserId();

        TextView mBewertung = (TextView) findViewById(R.id.Bewertung);
        TextView mTitel = (TextView) findViewById(R.id.Titel);
        TextView mPreis = (TextView) findViewById(R.id.Preis);
        CheckBox mCheckBox_vegan = (CheckBox)findViewById(R.id.checkBox_vegan);
        CheckBox mCheckBox_vegetarian = (CheckBox)findViewById(R.id.checkBox_vegetarian);
        final RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        final RatingBar mRatingBar2 = (RatingBar) findViewById(R.id.ratingBar3);
        ImageView btnCamera  = (ImageView)findViewById(R.id.btnCamera);
        sliderShow = (SliderLayout)findViewById(R.id.imageSlider);
        defaultSliderView = new DefaultSliderView(this);
        mCheckBox_vegan.setEnabled(false);
        mCheckBox_vegetarian.setEnabled(false);


        spinner = (Spinner)findViewById(R.id.fp_spinnerStandort);
        adapter = ArrayAdapter.createFromResource(this, R.array.StandorteMensa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String location = parent.getItemAtPosition(position).toString();

                switch (location){
                    case "Prittwitzstraße":
                        Standort = "Prittwitzstraße";
                        break;
                    case "Böfingen":
                        Standort = "Böfingen";
                        break;
                    case "Eselsberg":
                        Standort = "Eselsberg";                         //String Standort kann nun z.b. mit klicken auf Button Bewertung absenden auch hochgeladen werden
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }

        });



        final FoodClass food = getIntent().getParcelableExtra("food");

        int food_id = food.getId();
        String price = food.getPrice();
        String name = food.getName();
        int rating = food.getRating();
        String imageRes = food.getmimgId();
        int vegetarian = food.isVegetarian();
        int vegan = food.isVegan();

        mBewertung.setText(Integer.toString(rating));
        mRatingBar2.setRating(rating);
        mRatingBar2.setIsIndicator(true);

        new DownloadProfileImage().execute(Constants.ROOT_URL_PICTURES + imageRes);

        mRatingBar.setRating(rating);

        mPreis.setText(price);

        mTitel.setText(name);

        mBewertung.setText("Ø " + rating);

        DatabaseOperationsFetchRating get_rating = new DatabaseOperationsFetchRating(FoodProfile.this);
        get_rating.setAndGetRating(user_id, food_id, new IDatabaseOperationsFetchRating() {

            @Override
            public void onSuccess(String fetched_rating) {

                if(fetched_rating != null && !fetched_rating.isEmpty() && !fetched_rating.equals("null")) {
                    mRatingBar.setRating(Integer.parseInt(fetched_rating));
                }

            }

        });

        mRatingBar.setStepSize(1);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar mRatingBar, float user_rating, boolean fromUser) {
                if(user_rating<1){
                    mRatingBar.setRating(1);
                }
                int user_id = SharedPrefManager.getInstance(FoodProfile.this).getUserId();
                int food_id = food.getId();
                DatabaseOperationsSetRating new_rating = new DatabaseOperationsSetRating(FoodProfile.this);
                new_rating.setAndGetRating(user_id, food_id, Math.round(mRatingBar.getRating()));
            }


        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {takepicture();}
        });

        if(vegetarian == 1){
            mCheckBox_vegetarian.setChecked(true);
        }

        if(vegan==1){
            mCheckBox_vegan.setChecked(true);
        }

    }
    private void takepicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        imageDialog.setTitle(getString(R.string.uploadoptions));
        String [] imageDialogItems = {getString(R.string.gallery),getString(R.string.camera)};
        imageDialog.setItems(imageDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                choseImageFromGallery();
                                break;
                            case 1:
                                takeImageFromCamera();
                                break;
                        }
                    }
                });
        imageDialog.show();
    }

    private void choseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }
    private void takeImageFromCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == this.RESULT_CANCELED){
            return;
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null){
            image = (Bitmap)data.getExtras().get("data");
            this.UploadImageToServer();
        }else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            Uri contentURI = data.getData();
            try{
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
                this.UploadImageToServer();
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(FoodProfile.this,"Failed!",Toast.LENGTH_SHORT).show();;
            }
        }
    }

    public void UploadImageToServer(){
        image = this.getResizedBitmap(image,450,250);
        image.compress(Bitmap.CompressFormat.JPEG,40,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String convertImage = Base64.encodeToString(byteArray,Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String>{
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                progressDialog = ProgressDialog.show(FoodProfile.this,"Uploading","Pleas Wait");
            }
            @Override
            protected void onPostExecute(String string1){
                super.onPostExecute(string1);
                progressDialog.dismiss();
                Toast.makeText(FoodProfile.this,string1, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(Void...params){
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put(imageTag,"test1");
                HashMapParams.put(imageName,convertImage);
                String FinalData = imageProcessClass.ImageHttpRequest(Constants.ROOT_URL_PICTURES,HashMapParams);
                return FinalData;
            }
        }

        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{
        public String ImageHttpRequest(String requestURL,HashMap<String,String>PData){
        return "OK";
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    //onStop method imageSlider
    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    private class DownloadProfileImage extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls) {

            try {
                //Check if there is only one image available

                 if(urls[0].equals(root_url)){

                 }

                 else if (urls.length <2 ){

                     runOnUiThread(new Runnable() {

                         @Override
                         public void run() {

                             ImageView placeholder = (ImageView)findViewById(R.id.placeholder);
                             placeholder.setVisibility(View.GONE);

                         }
                     });

                    defaultSliderView
                            .image(urls[0])
                            .setScaleType(BaseSliderView.ScaleType.Fit);
                    sliderShow.addSlider(defaultSliderView);
                    //Deactivate autocycle and swipe gesture
                    sliderShow.stopAutoCycle();
                    sliderShow.setPagerTransformer(false, new BaseTransformer() {
                        @Override
                        protected void onTransform(View view, float position) {
                        }
                    });
                }else {
                    //Load every image fore this max. 10
                    for (int i = 0; i<urls.length;i++){
                        defaultSliderView
                                .image(urls[i])
                                .setScaleType(BaseSliderView.ScaleType.Fit);
                        sliderShow.addSlider(defaultSliderView);
                        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        if (i == 10) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {

                Log.d("Error","Image Download failed");

            }
            sliderShow.setDuration(7500);
            return null;
        }
    }
}
