package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_IMAGE;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import de.hsulm.mensaapp.Constants;
import de.hsulm.mensaapp.RequestHandler;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for handling all DB operations such as getting food
 */
public class DatabaseOperationsTransmitImages {

    private Context mContext;

    public DatabaseOperationsTransmitImages(Context context) {
        mContext = context;
    }


    public void uploadImageToDB(final String img_enc, final int user_id, final int food_id, final IDatabaseOperationsTransmitImages callback) {

        //Need to be converted to String because Parsing-Hashmap only takes Strings
        final String user_id_str = ((Integer)user_id).toString();
        final String food_id_str = ((Integer)food_id).toString();

        StringRequest arrayRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_TRANSMIT_IMG,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess();
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
                params.put("user_id", user_id_str);
                params.put("food_id",food_id_str);
                params.put("img_enc",img_enc);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(arrayRequest);

    }

}


