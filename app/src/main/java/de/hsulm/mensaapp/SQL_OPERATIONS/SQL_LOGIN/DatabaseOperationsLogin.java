package de.hsulm.mensaapp.SQL_OPERATIONS.SQL_LOGIN;

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
import de.hsulm.mensaapp.SHARED_PREF_MANAGER.SharedPrefManager;
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_REQUEST_HANDLER.RequestHandler;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for login
 */
public class DatabaseOperationsLogin {

    private Context mContext;

    public DatabaseOperationsLogin(Context context) {
        mContext = context;
    }


    public void checkLoginCredentials(final String username, final String password, final IDatabaseOperationsLogin callback) {

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URLS.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                SharedPrefManager.getInstance(mContext).userLogin(obj.getInt("id"), obj.getString("username"), obj.getString("email"));
                                callback.onSuccess();
                            }else{
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                        callback.onError();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

        };

        RequestHandler.getInstance(mContext).addToRequestQueue(stringRequest);

    }

}





