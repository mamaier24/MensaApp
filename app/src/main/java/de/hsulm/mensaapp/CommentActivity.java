package de.hsulm.mensaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hsulm.mensaapp.ANDROID_IS_ONLINE.Connection;
import de.hsulm.mensaapp.JAVA_ID_AND_DATE_TIME.DateID;
import de.hsulm.mensaapp.FoodClass;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    public String Standort=null;

    private MultiAutoCompleteTextView MultiTextComment;
    private Button pushRating;
    private ProgressDialog progressDialog;
    private ProgressBar progessBar_register;

    //IDs etc. mit in tabelle hochladen UN,Standort,Datum würde ich auch gleich mitgeben...mySQL Tabelle anpassen!
    int user_id = SharedPrefManager.getInstance(this).getUserId();

    String Username = SharedPrefManager.getInstance(this).getUsername();

    //Intent getCommentIntent = getIntent();
    //String food_id = getCommentIntent.getStringExtra("food_id");

    //final FoodClass food_id = getIntent().getParcelableExtra("food");
    //int i_food_id = food_id.getId();
    int i_food_id = 1; //Test Parcelable funktioniert nicht...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);



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
                    case "none":
                        Standort = "none";
                        break;
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

        MultiTextComment = (MultiAutoCompleteTextView) findViewById(R.id.mACTComment);
        pushRating = (Button) findViewById(R.id.bpushRating);

        progessBar_register = (ProgressBar)findViewById(R.id.progressBar_register);
        progessBar_register.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);

        pushRating.setOnClickListener(this);
        pushRating.setTransformationMethod(null);

    }

    private void pushComment() {
        final String s_user_id = Integer.toString(user_id);
        final String s_food_id = Integer.toString(i_food_id);
        final String Comment = MultiTextComment.getText().toString().trim();
        final String standort = Standort;
        final String username = Username;

        if (Standort.length() > 5) {

                    progressDialog.setMessage("Bewertung abgeben...");
                    progressDialog.show();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Constants.URL_PUSH_COMMENT,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                        if (jsonObject.getString("error") == "false") {

                                            onNotError();

                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();


                                }

                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", s_user_id);
                            params.put("food_id", s_food_id);
                            params.put("comments", Comment);
                            params.put("location", standort);
                            params.put("username", username);
                            //params.put("date", date);
                            return params;
                        }
                    };


                    RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

                } else {
                    Toast.makeText(getApplicationContext(), "Bitte Standort auswählen!", Toast.LENGTH_LONG).show();
                }
    }


    @Override
    public void onClick(View view) {

        if (Connection.getInstance().isOnline(this)) {

            progessBar_register.setVisibility(View.VISIBLE);
            pushRating.setEnabled(false);

            if (view == pushRating) {
                pushComment();
            }

            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pushRating.setEnabled(true);
                            progessBar_register.setVisibility(View.GONE);
                        }
                    });
                }

            }, 5000);

        }else{
            Toast.makeText(getApplicationContext(), "Du bist nicht mit dem Internet verbunden!", Toast.LENGTH_LONG).show();
        }

    }

    public void onNotError(){
        startActivity(new Intent(this, FoodProfile.class));
    }


}
