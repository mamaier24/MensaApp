package de.hsulm.mensaapp.SQL_FETCH_OR_CREATE_RATING;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hsulm.mensaapp.Constants;
import de.hsulm.mensaapp.RequestHandler;

public class DatabaseOperationsFetch {

    private Context mContext;
    private int rating;
    private IDatabaseOperationsFetch callback;

    public DatabaseOperationsFetch(Context context) {
        mContext = context;
    }


    public void setAndGetRating(int user_id, final int food_id) {

        final String user_id_string = ((Integer)user_id).toString();
        final String food_id_string = ((Integer)food_id).toString();

        StringRequest arrayRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_GET_RATING,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject rating_obj = new JSONObject(response);

                            rating = rating_obj.getInt("user_rating");
                            callback.onSuccess(rating);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", user_id_string);
                params.put("food_id", food_id_string);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(arrayRequest);

    }

}
