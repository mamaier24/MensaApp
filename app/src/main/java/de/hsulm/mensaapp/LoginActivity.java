package de.hsulm.mensaapp;

/**
 * Created by Marcel Maier on 30/11/18.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import de.hsulm.mensaapp.ANDROID_IS_ONLINE.Connection;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar_login;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, UserAreaActivity.class));
            return;
        }

        editTextUsername = (EditText) findViewById(R.id.eTUsername);
        editTextPassword = (EditText) findViewById(R.id.eTPassword);
        buttonLogin = (Button) findViewById(R.id.bLogin);
        progressBar_login = (ProgressBar)findViewById(R.id.progressBar_login);
        registerLink = (TextView)findViewById(R.id.tVRegister);

        progressBar_login.setVisibility(View.GONE);
        buttonLogin.setTransformationMethod(null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);

    }

    private void userLogin(){

        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getInt("id"),
                                                obj.getString("username"),
                                                obj.getString("email")
                                        );
                                startActivity(new Intent(getApplicationContext(), UserAreaActivity.class));
                                finish();
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        obj.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Toast.makeText(
                                getApplicationContext(),
                               error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    //Deactivate loginbutton to avoid login spamming
    //Activate progressbar to symbol activity
    @Override
    public void onClick(View view) {

        if (view == registerLink)
            startActivity(new Intent(this, RegisterActivity.class));

        else if(Connection.getInstance().isOnline(this)) {

            progressBar_login.setVisibility(View.VISIBLE);

            buttonLogin.setEnabled(false);

            if (view == buttonLogin)
                userLogin();

            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            buttonLogin.setEnabled(true);
                            progressBar_login.setVisibility(View.GONE);
                        }
                    });
                }

            }, 5000);

        }else{
            Toast.makeText(getApplicationContext(), "Du bist nicht mit dem Internet verbunden!", Toast.LENGTH_LONG).show();
        }

    }

}
