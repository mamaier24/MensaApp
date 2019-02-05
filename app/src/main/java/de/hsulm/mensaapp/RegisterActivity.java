package de.hsulm.mensaapp;

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

import de.hsulm.mensaapp.CONNECTION_STATUS.Connection;
import de.hsulm.mensaapp.CONSTANTS.URLS;
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_REQUEST_HANDLER.RequestHandler;
import de.hsulm.mensaapp.SHARED_PREF_MANAGER.SharedPrefManager;

/**
 * Created by Marcel Maier on 30/11/18.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etMail, etPassword;
    private Button btnRegister;
    private ProgressDialog progressDialog;
    private ProgressBar pbRegister;
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

        etMail = (EditText) findViewById(R.id.etMail);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        pbRegister = (ProgressBar)findViewById(R.id.pbRegister);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        pbRegister.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        btnRegister.setOnClickListener(this);
        btnRegister.setTransformationMethod(null);

    }


    private void registerUser() {
        final String email = etMail.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (username.length() > 5 || username.isEmpty() || email.isEmpty() || password.isEmpty()) {

            if ( password.length() > 5 || username.isEmpty() || email.isEmpty() || password.isEmpty()) {

                if (isValidEmail(email) || email.isEmpty() || password.isEmpty() || username.isEmpty()) {

                    progressDialog.setMessage("Benutzer registrieren...");
                    progressDialog.show();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            URLS.URL_REGISTER,
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

            }else {
                Toast.makeText(getApplicationContext(), "Passwort zu kurz! Min. 6 Zeichen!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Benutzername zu kurz! Min. 6 Zeichen!", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onClick(View view) {

        if (Connection.getInstance().isOnline(this)) {

            pbRegister.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);

            if (view == btnRegister) {
                registerUser();
            }

            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            btnRegister.setEnabled(true);
                            pbRegister.setVisibility(View.GONE);
                        }
                    });
                }

            }, 5000);

        }else{
            Toast.makeText(getApplicationContext(), "Du bist nicht mit dem Internet verbunden!", Toast.LENGTH_LONG).show();
        }

    }


    public void onNotError(){
        startActivity(new Intent(this, LoginActivity.class));
    }


    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
