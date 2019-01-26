package de.hsulm.mensaapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import de.hsulm.mensaapp.ANDROID_IS_ONLINE.Connection;
import de.hsulm.mensaapp.SHARED_PREF_MANAGER_AND_REQUEST_HANDLER.SharedPrefManager;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT.DatabaseOperationsTransmitComments;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT.IDatabaseOperationsTransmitComments;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING.DatabaseOperationsFetchRating;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING.IDatabaseOperationsFetchRating;

/**
 * Created by Marcel Maier on 30/11/18.
 */
public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;

    private EditText MultiTextComment;
    private Button btnTransmitComment;
    private ProgressDialog progressDialog;

    private String user_id_str = Integer.toString(SharedPrefManager.getInstance(this).getUserId());
    private int user_id = SharedPrefManager.getInstance(this).getUserId();
    private String username = SharedPrefManager.getInstance(this).getUsername();
    private DialogInterface.OnClickListener dialogClickListener;
    private int food_id;
    private String food_id_str;
    private String comment;
    private RatingBar ratingBar;
    private String location =null;
    private String user_rating_str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        food_id = getIntent().getIntExtra("food_id",0);
        food_id_str = Integer.toString(getIntent().getIntExtra("food_id", 0));
        user_rating_str = Integer.toString(Math.round(getIntent().getFloatExtra("user_rating", 0.0f)));

        MultiTextComment = (EditText) findViewById(R.id.mACTComment);
        btnTransmitComment = (Button) findViewById(R.id.bpushRating);
        spinner = (Spinner)findViewById(R.id.fp_spinnerStandort);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        ratingBar.setIsIndicator(true);

        adapter = ArrayAdapter.createFromResource(this, R.array.StandorteMensa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String location = parent.getItemAtPosition(position).toString();

                switch (location){
                    case "-":
                        CommentActivity.this.location = "-";
                        break;
                    case "Prittwitzstraße":
                        CommentActivity.this.location = "Prittwitzstraße";
                        break;
                    case "Böfingen":
                        CommentActivity.this.location = "Böfingen";
                        break;
                    case "Eselsberg":
                        CommentActivity.this.location = "Eselsberg";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }

        });

        progressDialog = new ProgressDialog(this);

        btnTransmitComment.setOnClickListener(this);
        btnTransmitComment.setTransformationMethod(null);

        initializeRating();
        initializeDialog();

    }


    @Override
    public void onClick(View view) {
        if (view == btnTransmitComment) {
            if (Connection.getInstance().isOnline(this)) {
                if (location.length() > 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Bist du sicher dass du den Kommentar senden willst?").setPositiveButton("Ja", dialogClickListener)
                            .setNegativeButton("Nein", dialogClickListener).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bitte Standort auswählen!", Toast.LENGTH_LONG).show();
                }
            }else{
                    Toast.makeText(getApplicationContext(), "Du bist nicht mit dem Internet verbunden!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Fetch rating and initialize ratingbarOnChangeListener
     */
    public void initializeRating() {
        /**
         * Fetches the unique user rating from DB.
         */
        DatabaseOperationsFetchRating get_rating = new DatabaseOperationsFetchRating(CommentActivity.this);
        get_rating.setAndGetRating(user_id, food_id, new IDatabaseOperationsFetchRating() {

            @Override
            public void onSuccess(String fetched_rating) {

                if (fetched_rating != null && !fetched_rating.isEmpty() && !fetched_rating.equals("null")) {
                    ratingBar.setRating(Integer.parseInt(fetched_rating));
                }

            }

        });

    }

    private void initializeDialog(){
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        transmitComment();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }

    /**
     * Transmits the comment to the DB
     */
    private void transmitComment() {

        comment = MultiTextComment.getText().toString().trim();


                    progressDialog.setMessage("Bewertung abgeben...");
                    progressDialog.show();
                    DatabaseOperationsTransmitComments new_comment = new DatabaseOperationsTransmitComments(this);

                    new_comment.transmitCommentToDB(user_id_str, food_id_str, comment, location, username, user_rating_str, progressDialog, new IDatabaseOperationsTransmitComments() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }
                    });


    }

}
