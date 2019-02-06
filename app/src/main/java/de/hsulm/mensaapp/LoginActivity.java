package de.hsulm.mensaapp;

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

import de.hsulm.mensaapp.CONNECTION_STATUS.Connection;
import de.hsulm.mensaapp.CONSTANTS.URLS;
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_LOGIN.DatabaseOperationsLogin;
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_LOGIN.IDatabaseOperationsLogin;
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_REQUEST_HANDLER.RequestHandler;
import de.hsulm.mensaapp.SHARED_PREF_MANAGER.SharedPrefManager;

/**
 * Created by Marcel Maier on 30/11/18.
 * Class which handles the user login
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private ProgressBar pbLogin;
    private TextView tvRegister;
    private DatabaseOperationsLogin login = new DatabaseOperationsLogin(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, UserAreaActivity.class));
            return;
        }

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);
        tvRegister = (TextView)findViewById(R.id.tvRegister);

        pbLogin.setVisibility(View.GONE);
        btnLogin.setTransformationMethod(null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }


    /**
     * Method which is calling the DB request
     */
    private void userLogin(){
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        progressDialog.show();

        login.checkLoginCredentials(username, password, new IDatabaseOperationsLogin() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, UserAreaActivity.class));
                finish();
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
            }
        });
    }


    //Deactivate loginbutton to avoid login spamming
    //Activate progressbar to symbol activity
    @Override
    public void onClick(View view) {
        if (view == tvRegister)
            startActivity(new Intent(this, RegisterActivity.class));

        else if(Connection.getInstance().isOnline(this)) {

            pbLogin.setVisibility(View.VISIBLE);

            btnLogin.setEnabled(false);

            if (view == btnLogin)
                userLogin();

            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            btnLogin.setEnabled(true);
                            pbLogin.setVisibility(View.GONE);
                        }
                    });
                }

            }, 5000);

        }else{
            Toast.makeText(getApplicationContext(), "Du bist nicht mit dem Internet verbunden!", Toast.LENGTH_LONG).show();
        }
    }

}
