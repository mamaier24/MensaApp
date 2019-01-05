package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hsulm.mensaapp.Constants;
import de.hsulm.mensaapp.RequestHandler;

public class DatabaseOperationsFetchRating {

    private Context mContext;
    private String rating;

    public DatabaseOperationsFetchRating(Context context) { mContext = context; }


    public void setAndGetRating(int user_id, final int food_id, final IDatabaseOperationsFetchRating callback) {

        final String user_id_string = ((Integer)user_id).toString();
        final String food_id_string = ((Integer)food_id).toString();

        StringRequest arrayRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_FETCH_RATING,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject rating_obj = new JSONObject(response);

                            rating = rating_obj.getString("user_rating");
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
