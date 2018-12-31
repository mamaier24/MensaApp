package de.hsulm.mensaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


import de.hsulm.mensaapp.ANDROID_IS_ONLINE.Connection;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_COMMENT.DatabaseOperationsComments;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_COMMENT.IDatabaseOperationsComments;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.DatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.DatabaseOperationsSetRating;
import de.hsulm.mensaapp.SQL_SET_OR_FETCH_RATING.IDatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_UPLOAD_IMAGE.DatabaseOperationsFetchImages;
import de.hsulm.mensaapp.SQL_UPLOAD_IMAGE.IDatabaseOperationsFetchImages;

import static de.hsulm.mensaapp.R.layout.activity_food_profile;

public class FoodProfile extends AppCompatActivity implements View.OnClickListener{

    private SliderLayout sliderShow;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 0;
    private Bitmap image;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private Button button_comment;
    private ImageView btnCamera;
    private TextView ratingAvg;
    private RecyclerView recyclerView;
    private FoodClass food = null;
    private int food_id;
    private RatingBar mRatingBar;
    private RecyclerView.Adapter adapter;
    private DatabaseOperationsComments operations = new DatabaseOperationsComments(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_food_profile);

        int user_id = SharedPrefManager.getInstance(FoodProfile.this).getUserId();
        final FoodClass food = getIntent().getParcelableExtra("food");

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
        String[] urls_test = new String[2];
        urls_test[0] = Constants.ROOT_URL_PICTURES + "MA_PIC_ID_A1XX.png";
        urls_test[1] = Constants.ROOT_URL_PICTURES + "MA_PIC_ID_B2XXX.png";

        new DownloadProfileImage().execute(urls_test);

        mRatingBar.setRating(0);
        ratingAvg.setText(String.valueOf(rating));

        mPreis.setText(price);

        mTitel.setText(name);

        DatabaseOperationsFetchImages get_images = new DatabaseOperationsFetchImages(FoodProfile.this);
        get_images.getImagesFromDB(food_id, new IDatabaseOperationsFetchImages() {
            @Override
            public void onSuccess(ArrayList img_id_list) {
                //new DownloadProfileImage().execute(img_id_list);
            }
        });

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


    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerComments(food_id);
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
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String img_enc = Base64.encodeToString(byteArray,Base64.DEFAULT);

        //@FELIX HERMANN ADD THESE LINES FOR IMAGE UPLOAD
        //DatabaseOperationsSetImages connection = new DatabaseOperationsSetImages(FoodProfile.this);
        //connection.uploadImageToDB(img_enc, user_id, food_id);
        //image = null;
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


    private class DownloadProfileImage extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls) {

            try {
                //Check if there is only one image available

                if (urls.length <2 ){

                    DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfile.this);

                     runOnUiThread(new Runnable() {

                         @Override
                         public void run() {

                             ImageView placeholder = (ImageView)findViewById(R.id.placeholder);
                             placeholder.setVisibility(View.GONE);

                         }
                     });

                    defaultSliderView.image(urls[0]);
                    defaultSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                    sliderShow.addSlider(defaultSliderView);
                    //Deactivate autocycle and swipe gesture
                    sliderShow.stopAutoCycle();
                    sliderShow.setPagerTransformer(false, new BaseTransformer() {
                        @Override
                        protected void onTransform(View view, float position) {
                        }
                    });

                }else {

                     runOnUiThread(new Runnable() {

                         @Override
                         public void run() {

                             ImageView placeholder = (ImageView)findViewById(R.id.placeholder);
                             placeholder.setVisibility(View.GONE);

                         }
                     });

                    for (int i = 0; i < urls.length; i++){
                        DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfile.this);
                        defaultSliderView.image(urls[i]);
                        defaultSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
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