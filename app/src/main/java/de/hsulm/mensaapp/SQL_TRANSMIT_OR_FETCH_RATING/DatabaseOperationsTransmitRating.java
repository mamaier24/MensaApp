package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_RATING;

import android.content.Context;
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

import de.hsulm.mensaapp.CONSTANTS.URLS;
import de.hsulm.mensaapp.SHARED_PREF_MANAGER_AND_REQUEST_HANDLER.RequestHandler;

/**
 * Created by Marcel Maier 30/12/2018
 * Class necessary for transmission of a rating of specific food
 */
public class DatabaseOperationsTransmitRating {

    private Context mContext;

    public DatabaseOperationsTransmitRating(Context context) {
        mContext = context;
    }


    public void setAndGetRating(int user_id, final int food_id, final int user_rating, final IDatabaseOperationsTransmitRating callback) {

        final String user_id_string = ((Integer)user_id).toString();
        final String food_id_string = ((Integer)food_id).toString();
        final String user_rating_string = ((Integer)user_rating).toString();


        StringRequest arrayRequest = new StringRequest(
                Request.Method.POST,
                URLS.URL_TRANSMIT_RATING,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject rating_obj = new JSONObject(response);

                            if (!rating_obj.getBoolean("error") && !rating_obj.getString("message").equals("alreadyRated")) {
                                Toast.makeText(mContext, rating_obj.getString("message"), Toast.LENGTH_LONG).show();
                            } else if (rating_obj.getBoolean("error")) {
                                Toast.makeText(mContext, "Fehler bei der Bewertung!", Toast.LENGTH_LONG).show();
                            }

                            callback.onSuccess();

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
                params.put("user_rating",user_rating_string);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(arrayRequest);

    }

}
