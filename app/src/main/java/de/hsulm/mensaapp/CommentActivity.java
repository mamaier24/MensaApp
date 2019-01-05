package de.hsulm.mensaapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import de.hsulm.mensaapp.ANDROID_IS_ONLINE.Connection;
import de.hsulm.mensaapp.SHARED_PREF_MANAGER_AND_REQUEST_HANDLER.SharedPrefManager;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT.DatabaseOperationsTransmitComments;
import de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT.IDatabaseOperationsTransmitComments;

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
    private String food_id_str;
    private String comment;
    private String location =null;
    private String username = SharedPrefManager.getInstance(this).getUsername();
    private String user_rating_str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        food_id_str = Integer.toString(getIntent().getIntExtra("food_id", 0));
        user_rating_str = Integer.toString(Math.round(getIntent().getFloatExtra("user_rating", 0.0f)));

        MultiTextComment = (EditText) findViewById(R.id.mACTComment);
        btnTransmitComment = (Button) findViewById(R.id.bpushRating);
        spinner = (Spinner)findViewById(R.id.fp_spinnerStandort);

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

    }


    @Override
    public void onClick(View view) {

        if (view == btnTransmitComment) {
            transmitComment();
        }

    }


    private void transmitComment() {

        comment = MultiTextComment.getText().toString().trim();

        if (Connection.getInstance().isOnline(this)) {
            if (location.length() > 5) {
                    progressDialog.setMessage("Bewertung abgeben...");
                    progressDialog.show();
                    DatabaseOperationsTransmitComments new_comment = new DatabaseOperationsTransmitComments(this);

                    new_comment.transmitCommentToDB(user_id_str, food_id_str, comment, location, username, user_rating_str, progressDialog, new IDatabaseOperationsTransmitComments() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }
                    });
            } else {
                Toast.makeText(getApplicationContext(), "Bitte Standort auswählen!", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Du bist nicht mit dem Internet verbunden!", Toast.LENGTH_LONG).show();
        }

    }

}
