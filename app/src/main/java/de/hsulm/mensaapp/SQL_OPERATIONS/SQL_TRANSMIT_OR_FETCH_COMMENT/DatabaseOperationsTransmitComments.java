package de.hsulm.mensaapp.SQL_OPERATIONS.SQL_TRANSMIT_OR_FETCH_COMMENT;


import android.app.ProgressDialog;
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
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_REQUEST_HANDLER.RequestHandler;


/**
 * Created by Marcel Maier 30/12/2018
 * Class necessary for transmission of comments
 */
public class DatabaseOperationsTransmitComments {

    private Context context;

    public DatabaseOperationsTransmitComments(Context context) {
        this.context = context;
    }

    public void transmitCommentToDB(final String user_id_str, final String  food_id_str, final String  comment, final String location,
                                    final String username, final String user_rating_str, final ProgressDialog progressDialog, final IDatabaseOperationsTransmitComments callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.URL_TRANSMIT_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            if (object.getString("error") == "false") {
                                progressDialog.dismiss();
                                Toast.makeText(context, object.getString("message"), Toast.LENGTH_LONG).show();
                                callback.onSuccess();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(context, object.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(context, "Es ist ein Fehler beim Senden des Kommentars aufgetreten!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id_str);
                params.put("food_id", food_id_str);
                params.put("comments", comment);
                params.put("location", location);
                params.put("username", username);
                params.put("user_rating", user_rating_str);
                return params;
            }

        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }

}

