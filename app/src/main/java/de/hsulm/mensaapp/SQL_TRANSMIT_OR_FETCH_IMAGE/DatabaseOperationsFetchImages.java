package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_IMAGE;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hsulm.mensaapp.Constants;
import de.hsulm.mensaapp.RequestHandler;

public class DatabaseOperationsFetchImages {

    private Context mContext;

    public DatabaseOperationsFetchImages(Context context) { mContext = context; }


    public void getImagesFromDB(final int food_id, final IDatabaseOperationsFetchImages callback) {

        final String food_id_string = ((Integer)food_id).toString();

        StringRequest arrayRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_FETCH_IMG,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        final ArrayList<String> img_id_list = new ArrayList<>();

                        try {

                            JSONArray img_ids_arr = new JSONArray(response);

                            for (int i = 0; i < img_ids_arr.length(); i++) {
                                JSONObject img_id_obj = img_ids_arr.getJSONObject(i);
                                String img_id = img_id_obj.getString("img_id");
                                img_id_list.add(img_id);
                            }

                            callback.onSuccess(img_id_list);

                        } catch (JSONException e) {
                            callback.onSuccess(img_id_list);
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
                params.put("food_id", food_id_string);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(arrayRequest);

    }

}
