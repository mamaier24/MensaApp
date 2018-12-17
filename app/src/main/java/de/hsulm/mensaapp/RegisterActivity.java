package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private ProgressDialog progressDialog;
    private ProgressBar progessBar_register;
    private TextView textViewLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, UserAreaActivity.class));
            return;
        }

        editTextEmail = (EditText) findViewById(R.id.eTEmail);
        editTextUsername = (EditText) findViewById(R.id.eTUsername);
        editTextPassword = (EditText) findViewById(R.id.eTPassword);
        progessBar_register = (ProgressBar)findViewById(R.id.progressBar_register);
        progessBar_register.setVisibility(View.GONE);


        buttonRegister = (Button) findViewById(R.id.bRegister);

        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);
        buttonRegister.setTransformationMethod(null);

    }


    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (username.length() > 6 || username.isEmpty()) {

            if (isValidEmail(email) || email.isEmpty()) {

                progressDialog.setMessage("Benutzer registrieren...");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Constants.URL_REGISTER,
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
                        params.put("username", username);
                        params.put("email", email);
                        params.put("password", password);
                        return params;
                    }
                };


                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

            } else {
                Toast.makeText(getApplicationContext(), "Fehlerhafte E-Mail-Adresse!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Benutzername zu kurz!", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onClick(View view) {

        progessBar_register.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);

        if (view == buttonRegister){
            registerUser();
        }

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        buttonRegister.setEnabled(true);
                        progessBar_register.setVisibility(View.GONE);
                    }
                });
            }

        }, 5000);

    }


    public void onNotError(){
        startActivity(new Intent(this, LoginActivity.class));
    }


    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
