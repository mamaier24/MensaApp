package de.hsulm.mensaapp;

import android.app.IntentService;
import android.content.Intent;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for handling all DB operations such as getting food
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
