package de.hsulm.mensaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.Menu;
import android.view.MenuItem;
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
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.CommentsAdapter;
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.CommentsClass;
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.FoodClass;
import de.hsulm.mensaapp.CONSTANTS.URLS;
import de.hsulm.mensaapp.JAVA_ID_AND_DATE_TIME.DateID;
import de.hsulm.mensaapp.SHARED_PREF_MANAGER_AND_REQUEST_HANDLER.SharedPrefManager;
import de.hsulm.mensaapp.SQL_SEARCH_BY_ID.DatabaseOperationsID;
import de.hsulm.mensaapp.SQL_SEARCH_BY_ID.IDatabaseOperationsID;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT.DatabaseOperationsFetchComments;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT.IDatabaseOperationsFetchComments;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING.DatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING.DatabaseOperationsTransmitRating;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING.IDatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_IMAGE.DatabaseOperationsFetchImages;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_IMAGE.DatabaseOperationsTransmitImages;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_IMAGE.IDatabaseOperationsFetchImages;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_IMAGE.IDatabaseOperationsTransmitImages;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING.IDatabaseOperationsTransmitRating;

import static de.hsulm.mensaapp.R.layout.activity_food_profile;

/**
 * Created by Marcel Maier on 30/11/18.
 */
public class FoodProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private int user_id;
    private int food_id;
    private int position;
    private static final int REQUEST_CAPTURE_IMAGE  = 100;
    private static final int GALLERY_REQUEST = 0;
    private String imageFilePath;
    private ByteArrayOutputStream byteArrayOutputStream;

    private Button btnComment;
    private ImageView btnCamera;
    private TextView ratingAvg;
    private TextView uiE_number_rating;
    private RecyclerView recyclerView;
    private RatingBar mRatingBar;
    private RatingBar mRatingBar2;
    private SliderLayout sliderShow;
    private Bitmap image;

    private RecyclerView.Adapter adapter;
    private FoodClass food = null;
    private DatabaseOperationsFetchComments operationsComments = new DatabaseOperationsFetchComments(this);
    private DatabaseOperationsID operationsID = new DatabaseOperationsID(this);
    private DateID time = new DateID();
    private String prev_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_food_profile);


        /**
         * Get extras from intent.
         */
        food = getIntent().getParcelableExtra("food");
        prev_intent = getIntent().getStringExtra("intent");
        position = getIntent().getIntExtra("position", 0);


        /**
         * @food_id Is initialized after the intent gets all necessary extras.
         * @user_id Needs to be initialized in onCreate()-method because it needs the app context.
         */
        food_id = food.getId();
        user_id = SharedPrefManager.getInstance(FoodProfileActivity.this).getUserId();


        /**
         * Variables with method space init.
         */
        String price = food.getPrice();
        String name = food.getName()+"*";
        String imageRes = food.getmimgId();
        int number_rating = food.getNumberRating();
        int rating = food.getRating();
        int vegetarian = food.isVegetarian();
        int vegan = food.isVegan();


        /**
         * UI elements with method space init.
         */
        TextView mTitel = (TextView) findViewById(R.id.tVComment);
        TextView mPreis = (TextView) findViewById(R.id.Preis);
        CheckBox mCheckBox_vegan = (CheckBox)findViewById(R.id.checkBox_vegan);
        CheckBox mCheckBox_vegetarian = (CheckBox)findViewById(R.id.checkBox_vegetarian);


        /**
         * UI elements with class space init.
         */
        ratingAvg = (TextView)findViewById(R.id.tVratingAVG);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        mRatingBar2 = (RatingBar) findViewById(R.id.ratingBar);
        sliderShow = (SliderLayout)findViewById(R.id.imageSlider);
        btnCamera  = (ImageView)findViewById(R.id.btnCamera);
        btnComment = (Button)findViewById(R.id.bWroteComment);
        uiE_number_rating = (TextView)findViewById(R.id.uiE_number_rating);


        /**
         * UI state init.
         */
        mCheckBox_vegan.setClickable(false);
        mCheckBox_vegetarian.setClickable(false);
        btnComment.setTransformationMethod(null);
        btnComment.setEnabled(false);
        btnCamera.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        mRatingBar.setRating(0);
        mRatingBar.setStepSize(1);
        mRatingBar2.setRating(rating);
        mRatingBar2.setIsIndicator(true);
        ratingAvg.setText(String.valueOf(rating));
        mPreis.setText(price);
        mTitel.setText(name);
        uiE_number_rating.setText("( " + String.valueOf(number_rating) + " )");

        if(vegetarian == 1){
            mCheckBox_vegetarian.setChecked(true);
            if(vegan==1){
                mCheckBox_vegan.setChecked(true);
            }
        }


        /**
         * Fetches all images from DB if the previous intent was UserAreaActivity.
         */
        if(prev_intent.equals("UserAreaActivity") || prev_intent.equals("SearchActivity")) {
            initializeImageSlider();
        }


        /**
         * Fetch rating and initialize ratingbarOnChangeListener
         */
        initializeRating();


        /**
         * Fetches and initializes all comments
         */
        initializeRecyclerComments(food.getId());

    }


    /**
     * Further application lifecycle methods
     */
    @Override
    protected void onStop() {

        sliderShow.stopAutoCycle();
        super.onStop();

    }


    /**
     * Creates new options menu to prevent unlimited intent creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (prev_intent.equals("SearchActivity")) {
            getMenuInflater().inflate(R.menu.menu_w_o_search, menu);
            return true;
        }else {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }
    }


    /**
     * Necessary code for the options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Speiseplan:
                Intent menuIntent = new Intent(this, MenuActivity.class);
                this.startActivity(menuIntent);
                break;

            case R.id.Inhaltsstoffe:
                Intent InhaltsstoffeIntent = new Intent(this, IngredientsActivity.class);
                this.startActivity(InhaltsstoffeIntent);
                break;


            case R.id.Lob_Tadel:
                Log.i("Want to Send Mail", "");
                String[] TO = {"mensa@studierendenwerk-ulm.de"};//Email
                String[] CC = new String[]{SharedPrefManager.getInstance(this).getUserEmail()};
                String UN = SharedPrefManager.getInstance(this).getUsername();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bitte geben sie Ihre Speise(n) an:\n");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hier ist Platz für Lob und konstruktive Kritik:\n\n\n\nViele Grüße " + UN);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Wählen Sie eine Email-App aus..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Kein Mailprogramm gefunden!", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.Suche:
                //Intent zum Suchen
                Intent sucheIntent = new Intent(this, SearchActivity.class);
                this.startActivity(sucheIntent);
                break;

            case R.id.Abmelden:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;

        }
        return true;
    }


    @Override
    public void onClick(View view) {

        if(view == btnComment) {
            Intent commentIntent = new Intent(FoodProfileActivity.this, CommentActivity.class);
            commentIntent.putExtra("food_id", food_id);
            commentIntent.putExtra("user_rating", mRatingBar.getRating());
            FoodProfileActivity.this.startActivity(commentIntent);
        }else if(view == btnCamera) {
            addPicture();
        }

    }


    @Override
    public void onResume() {

        initializeRecyclerComments(food_id);
        super.onResume();

    }


    /**
     * Section necessary for picture handling and upload
     */
    private void addPicture() {

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        imageDialog.setTitle(getString(R.string.uploadoptions));
        String [] imageDialogItems = {getString(R.string.gallery),getString(R.string.camera)};

        imageDialog.setItems(imageDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        takeImageFromGallery();
                        break;
                    case 1:
                        takeImageFromCamera();
                        break;
                }
            }
        });

        imageDialog.show();

    }


    private void takeImageFromGallery() {

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
                Toast.makeText(this, "Schreiben des Bildes auf Speicher fehlgeschlagen!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(FoodProfileActivity.this,"Upload fehlgeschlagen!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            this.UploadImageToServer();
        }else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            Uri contentURI = data.getData();
            try{
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
                this.UploadImageToServer();
            }catch (IOException e){
                Toast.makeText(FoodProfileActivity.this,"Upload fehlgeschlagen!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }


    public void UploadImageToServer(){

        byteArrayOutputStream = new ByteArrayOutputStream();
        image = this.getResizedBitmap(image);
        image.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String img_enc = Base64.encodeToString(byteArray,Base64.DEFAULT);
        DatabaseOperationsTransmitImages connection = new DatabaseOperationsTransmitImages(FoodProfileActivity.this);

        connection.uploadImageToDB(img_enc, user_id, food_id, new IDatabaseOperationsTransmitImages() {
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


    private class DownloadProfileImageOnStartup extends AsyncTask<ArrayList<String>, Void, Void> {

        protected Void doInBackground(final ArrayList<String>... images) {

            final ImageView placeholder = (ImageView)findViewById(R.id.placeholder);
            final ProgressBar progressbar = (ProgressBar)findViewById(R.id.progressBar_loadImages);

            try {

                if (images[0].size() <2 && images[0].size() != 0){

                    final DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfileActivity.this);

                     runOnUiThread(new Runnable() {

                         @Override
                         public void run() {

                             sliderShow.setVisibility(View.INVISIBLE);
                             sliderShow.removeAllSliders();
                             String url = URLS.ROOT_URL_PICTURES + images[0].get(0);
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

                                 DefaultSliderView defaultSliderView = new DefaultSliderView(FoodProfileActivity.this);
                                 String url = URLS.ROOT_URL_PICTURES + images[0].get(i);
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
                Toast.makeText(getApplicationContext(), "Slider konnte nicht initialisiert werden!", Toast.LENGTH_LONG).show();
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
                DatabaseOperationsFetchImages get_images = new DatabaseOperationsFetchImages(FoodProfileActivity.this);
                get_images.getImagesFromDB(food_id, new IDatabaseOperationsFetchImages() {
                    @Override
                    public void onSuccess(ArrayList<String> img_id_list) {
                        new DownloadProfileImageOnStartup().execute(img_id_list);
                    }
                });
            }
        }, 2000);

    }


    /**
     * Necessary for init of comment section
     */
    public void initializeRecyclerComments(int food_id) {

        operationsComments.getCommentsFromDB(Integer.toString(food_id), new IDatabaseOperationsFetchComments() {

            @Override
            public void onSuccess(final boolean isDefault, final ArrayList<CommentsClass> comments_list) {

                recyclerView = (RecyclerView) findViewById(R.id.recyclerViewComments);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(FoodProfileActivity.this));
                adapter = new CommentsAdapter(false, FoodProfileActivity.this, comments_list);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSuccess(final boolean isDefault) {

                recyclerView = (RecyclerView) findViewById(R.id.recyclerViewComments);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(FoodProfileActivity.this));
                adapter = new CommentsAdapter(true, FoodProfileActivity.this);
                recyclerView.setAdapter(adapter);

            }

        });

    }


    /**
     * Fetch rating and initialize ratingbarOnChangeListener
     */
    public void initializeRating(){
        /**
         * Fetches the unique user rating from DB.
         */
        DatabaseOperationsFetchRating get_rating = new DatabaseOperationsFetchRating(FoodProfileActivity.this);
        get_rating.setAndGetRating(user_id, food_id, new IDatabaseOperationsFetchRating() {

            @Override
            public void onSuccess(String fetched_rating) {

                if(fetched_rating != null && !fetched_rating.isEmpty() && !fetched_rating.equals("null")) {
                    mRatingBar.setRating(Integer.parseInt(fetched_rating));
                }

            }

        });

        /**
         * Transmits all changes of the ratingbar to the DB.
         */
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar mRatingBar, float user_rating, boolean fromUser) {
                if(user_rating<1){
                    mRatingBar.setRating(1);
                }
                if(!btnComment.isEnabled()){
                    btnComment.setEnabled(true);
                }
                int user_id = SharedPrefManager.getInstance(FoodProfileActivity.this).getUserId();
                final int food_id = food.getId();
                DatabaseOperationsTransmitRating new_rating = new DatabaseOperationsTransmitRating(FoodProfileActivity.this);

                new_rating.setAndGetRating(user_id, food_id, Math.round(mRatingBar.getRating()), new IDatabaseOperationsTransmitRating() {

                    @Override
                    public void onSuccess() {

                        operationsID.getFoodFromDB(Integer.toString(food_id), new IDatabaseOperationsID() {
                            @Override
                            public void onSuccess(FoodClass food) {
                                int number_rating = food.getNumberRating();
                                int rating = food.getRating();
                                mRatingBar2.setRating(rating);
                                uiE_number_rating.setText("( " + String.valueOf(number_rating) + " )");
                            }
                        });

                    }

                });

            }

        });

    }


    /**
     * Fetches all images from DB if the previous intent was UserAreaActivity.
     */
    public void initializeImageSlider(){

        DatabaseOperationsFetchImages get_images = new DatabaseOperationsFetchImages(FoodProfileActivity.this);
        get_images.getImagesFromDB(food_id, new IDatabaseOperationsFetchImages() {

            @Override
            public void onSuccess(ArrayList<String> img_id_list) {
                new DownloadProfileImageOnStartup().execute(img_id_list);
            }

        });

    }

}