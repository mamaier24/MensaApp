package de.hsulm.mensaapp;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkoeber on 06.03.14.
 */
public class DatabaseOperations extends IntentService {

    private static final String CLASS_NAME = DatabaseOperations .class.getName();
    public static final String CUSTOM_INTENT = DatabaseOperations .class.getName()+".Receiver";
    public static final String RESULT = "serviceResult";
    public static final String STATUS = "serviceStatus";
    public static final int STATE_FINAL = 20;

    public DatabaseOperations() {
        super("IntentTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final String searchQuery = intent.getStringExtra("searchQuery");

        StringRequest arrayRequest = new StringRequest (
                Request.Method.POST,
                Constants.URL_DB_OPS,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                        try {



                            JSONArray jsonArray = new JSONArray(response);
                            String food_object = jsonArray.toString();
                            publishStatus(food_object);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { }

                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("searchQuery", searchQuery);
                        return params;
                    }
        };

        RequestHandler.getInstance(this).addToRequestQueue(arrayRequest);

    }


    private void publishStatus(String food_object) {
        Intent intent = new Intent(CUSTOM_INTENT);
        intent.putExtra("food_object", food_object);
        getApplicationContext().sendBroadcast(intent);
    }
}
