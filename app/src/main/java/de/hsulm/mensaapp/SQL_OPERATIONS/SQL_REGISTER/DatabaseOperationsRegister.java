package de.hsulm.mensaapp.SQL_OPERATIONS.SQL_REGISTER;

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
 * Created by Stephan Danz 05/12/2018
 * Class necessary for registration
 */
public class DatabaseOperationsRegister {

    private Context mContext;

    public DatabaseOperationsRegister(Context context) {
        mContext = context;
    }

    public void checkRegistrationCredentials(final String username, final String password, final String email, final IDatabaseOperationsRegister callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLS.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                            if (jsonObject.getString("error") == "false") {
                                callback.onSuccess();
                            }else{
                                callback.onError();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError();
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(stringRequest);

    }

}
