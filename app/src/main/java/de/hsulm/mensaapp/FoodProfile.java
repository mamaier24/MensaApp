package de.hsulm.mensaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import de.hsulm.mensaapp.ANDROID_IS_ONLINE.Connection;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_COMMENT.DatabaseOperationsComments;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_COMMENT.IDatabaseOperationsComments;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.DatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.DatabaseOperationsSetRating;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.IDatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_UPLOAD_OR_FETCH_IMAGE.DatabaseOperationsFetchImages;
import de.hsulm.mensaapp.SQL_UPLOAD_OR_FETCH_IMAGE.DatabaseOperationsSetImages;
import de.hsulm.mensaapp.SQL_UPLOAD_OR_FETCH_IMAGE.IDatabaseOperationsFetchImages;
import de.hsulm.mensaapp.SQL_UPLOAD_OR_FETCH_IMAGE.IDatabaseOperationsSetImages;

import static de.hsulm.mensaapp.R.layout.activity_food_profile;

public class FoodProfile extends AppCompatActivity implements View.OnClickListener{

    private SliderLayout sliderShow;
    private static final int REQUEST_CAPTURE_IMAGE  = 100, GALLERY_REQUEST = 0;
    private Bitmap image;
    private ByteArrayOutputStream byteArrayOutputStream;
    private Button button_comment;
    private ImageView btnCamera;
    private TextView ratingAvg;
    private RecyclerView recyclerView;
    private FoodClass food = null;
    int user_id;
    String imageFilePath;
    private int food_id;
    private RatingBar mRatingBar;
    private RecyclerView.Adapter adapter;
    private DatabaseOperationsComments operations = new DatabaseOperationsComments(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_food_profile);

        user_id = SharedPrefManager.getInstance(FoodProfile.this).getUserId();
        final FoodClass food = getIntent().getParcelableExtra("food");
        final String previous_intent = getIntent().getStringExtra("intent");

        ratingAvg = (TextView)findViewById(R.id.tVratingAVG);
        TextView mTitel = (TextView) findViewById(R.id.tVComment);
        TextView mPreis = (TextView) findViewById(R.id.Preis);
        CheckBox mCheckBox_vegan = (CheckBox)findViewById(R.id.checkBox_vegan);
        CheckBox mCheckBox_vegetarian = (CheckBox)findViewById(R.id.checkBox_vegetarian);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        final RatingBar mRatingBar2 = (RatingBar) findViewById(R.id.ratingBar3);

        sliderShow = (SliderLayout)findViewById(R.id.imageSlider);
        mCheckBox_vegan.setClickable(false);
        mCheckBox_vegetarian.setClickable(false);

        button_comment = (Button)findViewById(R.id.bWroteComment);
        button_comment.setTransformationMethod(null);
        button_comment.setEnabled(false);
        btnCamera  = (ImageView)findViewById(R.id.btnCamera);

        food_id = food.getId();
        String price = food.getPrice();
        String name = food.getName()+"*";
        int rating = food.getRating();
        String imageRes = food.getmimgId();
        int vegetarian = food.isVegetarian();
        int vegan = food.isVegan();
        mRatingBar2.setRating(rating);
        mRatingBar2.setIsIndicator(true);

        mRatingBar.setRating(0);
        ratingAvg.setText(String.valueOf(rating));

        mPreis.setText(price);

        mTitel.setText(name);

        if(previous_intent.equals("UserAreaActivity")) {

            DatabaseOperationsFetchImages get_images = new DatabaseOperationsFetchImages(FoodProfile.this);
            get_images.getImagesFromDB(food_id, new IDatabaseOperationsFetchImages() {
                @Override
                public void onSuccess(ArrayList<String> img_id_list) {
                    new DownloadProfileImageOnStartup().execute(img_id_list);
                }
            });

        }


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
                if(!button_comment.isEnabled()){
                    button_comment.setEnabled(true);
                }
                int user_id = SharedPrefManager.getInstance(FoodProfile.this).getUserId();
                int food_id = food.getId();
                DatabaseOperationsSetRating new_rating = new DatabaseOperationsSetRating(FoodProfile.this);
                new_rating.setAndGetRating(user_id, food_id, Math.round(mRatingBar.getRating()));
            }


        });



        if(vegetarian == 1){
            mCheckBox_vegetarian.setChecked(true);
        }

        if(vegan==1){
            mCheckBox_vegan.setChecked(true);
        }

        btnCamera.setOnClickListener(this);
        button_comment.setOnClickListener(this);

        if(Connection.getInstance().isOnline(this)) {
            initializeRecyclerComments(food.getId());
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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }


    private void takeImageFromCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "de.hsulm.mensaapp.provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent,REQUEST_CAPTURE_IMAGE);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  ".jpg",  storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == this.RESULT_CANCELED){
            return;
        }
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK){
                Uri contentURI = Uri.fromFile(new File(imageFilePath));
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.UploadImageToServer();
        }else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            Uri contentURI = data.getData();
            try{
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
                this.UploadImageToServer();
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(FoodProfile.this,"Failed!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void UploadImageToServer(){
        byteArrayOutputStream = new ByteArrayOutputStream();
        image = this.getResizedBitmap(image);
        image.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String img_enc = Base64.encodeToString(byteArray,Base64.DEFAULT);
        DatabaseOperationsSetImages connection = new DatabaseOperationsSetImages(FoodProfile.this);

        connection.uploadImageToDB(img_enc, user_id, food_id, new IDatabaseOperationsSetImages() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Foto erfolgreich hochgeladen!", Toast.LENGTH_LONG).show();
                reinitializeSlider();
            }
        });

        image = null;

    }


    public Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int scaleWidth = Math.round(0.5f * width);
        int scaleHeight = Math.round(0.5f * height);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight,true);
        bm.recycle();
        return resizedBitmap;
    }


    //onStop method imageSlider
    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }


    @Override
    public void onClick(View view) {
        if(view == button_comment) {
            //startActivity(new Intent(this, CommentActivity.class));

            Intent CommentIntent = new Intent(FoodProfile.this, CommentActivity.class);
            CommentIntent.putExtra("food_id", food_id);
            CommentIntent.putExtra("user_rating", mRatingBar.getRating());
            FoodProfile.this.startActivity(CommentIntent);

        }else if(view == btnCamera) {takepicture();}
    }


    private class DownloadProfileImageOnStartup extends AsyncTask<ArrayList<String>, Void, Void> {
        protected Void doInBackground(final ArrayList<String>... images) {

            final ImageView placeholder = (ImageView)findViewById(R.id.placeholder);
            final ProgressBar progressbar = (ProgressBar)findViewById(R.id.progressBar_loadImages);

            try {

                if (images[0].size() <2 && images[0].size() != 0){

                    final DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfile.this);

                     runOnUiThread(new Runnable() {

                         @Override
                         public void run() {

                             sliderShow.setVisibility(View.INVISIBLE);
                             sliderShow.removeAllSliders();
                             String url = Constants.ROOT_URL_PICTURES + images[0].get(0);
                             defaultSliderView.image(url);
                             defaultSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                             sliderShow.addSlider(defaultSliderView);
                             sliderShow.stopAutoCycle();
                             sliderShow.setPagerTransformer(false, new BaseTransformer() {
                                 @Override
                                 protected void onTransform(View view, float position) {
                                 }
                             });

                             placeholder.setVisibility(View.GONE);
                             progressbar.setVisibility(View.GONE);
                             sliderShow.setVisibility(View.VISIBLE);

                         }
                     });

                }else if(images[0].size() >= 2 && images[0].size() != 0){

                     runOnUiThread(new Runnable() {

                         @Override
                         public void run() {

                             sliderShow.setVisibility(View.INVISIBLE);
                             sliderShow.removeAllSliders();

                             for (int i = 0; i < images[0].size(); i++){

                                 if (i == 10) {
                                     break;
                                 }

                                 DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfile.this);
                                 String url = Constants.ROOT_URL_PICTURES + images[0].get(i);
                                 defaultSliderView.image(url);
                                 defaultSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                 sliderShow.addSlider(defaultSliderView);
                                 sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

                             }


                             final Handler handler = new Handler();
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     placeholder.setVisibility(View.GONE);
                                     progressbar.setVisibility(View.GONE);
                                     sliderShow.setVisibility(View.VISIBLE);
                                 }
                             }, 1500);

                         }
                     });

                }else{

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressbar.setVisibility(View.GONE);
                        }
                    });

                }

            } catch (Exception e) {

                Log.d("Error","Image Download failed");

            }

            sliderShow.setDuration(10000);
            return null;
        }
    }


    private class DownloadProfileImageOnResume extends AsyncTask<ArrayList<String>, Void, Void> {
        protected Void doInBackground(final ArrayList<String>... images) {

            final ImageView placeholder = (ImageView)findViewById(R.id.placeholder);
            final ProgressBar progressbar = (ProgressBar)findViewById(R.id.progressBar_loadImages);

            try {

                if (images[0].size() <2 && images[0].size() != 0){



                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            placeholder.setVisibility(View.VISIBLE);
                            progressbar.setVisibility(View.VISIBLE);
                            final DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfile.this);
                            sliderShow.setVisibility(View.INVISIBLE);
                            sliderShow.removeAllSliders();
                            String url = Constants.ROOT_URL_PICTURES + images[0].get(0);
                            defaultSliderView.image(url);
                            defaultSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                            sliderShow.addSlider(defaultSliderView);
                            sliderShow.stopAutoCycle();
                            sliderShow.setPagerTransformer(false, new BaseTransformer() {
                                @Override
                                protected void onTransform(View view, float position) {
                                }
                            });

                            placeholder.setVisibility(View.GONE);
                            progressbar.setVisibility(View.GONE);
                            sliderShow.setVisibility(View.VISIBLE);
                            

                        }
                    });

                }else if(images[0].size() >= 2 && images[0].size() != 0){

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            placeholder.setVisibility(View.VISIBLE);
                            progressbar.setVisibility(View.VISIBLE);
                            sliderShow.setVisibility(View.INVISIBLE);
                            sliderShow.removeAllSliders();

                            for (int i = 0; i < images[0].size(); i++){

                                if (i == 10) {
                                    break;
                                }

                                DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfile.this);
                                String url = Constants.ROOT_URL_PICTURES + images[0].get(i);
                                defaultSliderView.image(url);
                                defaultSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                sliderShow.addSlider(defaultSliderView);
                                sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

                            }


                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    placeholder.setVisibility(View.GONE);
                                    progressbar.setVisibility(View.GONE);
                                    sliderShow.setVisibility(View.VISIBLE);
                                }
                            }, 1500);

                        }
                    });

                }else{

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressbar.setVisibility(View.GONE);
                        }
                    });

                }

            } catch (Exception e) {

                Log.d("Error","Image Download failed");

            }

            sliderShow.setDuration(10000);
            return null;
        }
    }


    public void reinitializeSlider(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseOperationsFetchImages get_images = new DatabaseOperationsFetchImages(FoodProfile.this);
                get_images.getImagesFromDB(food_id, new IDatabaseOperationsFetchImages() {
                    @Override
                    public void onSuccess(ArrayList<String> img_id_list) {
                        new DownloadProfileImageOnResume().execute(img_id_list);
                    }
                });
            }
        }, 2000);

    }


    public void initializeRecyclerComments(int food_id) {

        operations.getCommentsFromDB(Integer.toString(food_id), new IDatabaseOperationsComments() {
            @Override
            public void onSuccess(final boolean isDefault, final ArrayList<CommentsClass> comments_list) {

                recyclerView = (RecyclerView) findViewById(R.id.recyclerViewComments);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(FoodProfile.this));
                adapter = new CommentsAdapter(false, FoodProfile.this, comments_list);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSuccess(final boolean isDefault) {
                recyclerView = (RecyclerView) findViewById(R.id.recyclerViewComments);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(FoodProfile.this));
                adapter = new CommentsAdapter(true, FoodProfile.this);
                recyclerView.setAdapter(adapter);
            }

        });

    }

}