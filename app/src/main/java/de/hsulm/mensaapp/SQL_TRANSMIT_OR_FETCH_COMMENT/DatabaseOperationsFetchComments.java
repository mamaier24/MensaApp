package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT;


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
import de.hsulm.mensaapp.CLASS_OBJ.CommentsClass;

/**
 * Created by Marcel Maier 30/12/2018
 * Class necessary for handling all DB operations such as getting food
 */
public class DatabaseOperationsFetchComments {

    private Context context;

    public DatabaseOperationsFetchComments(Context context) {
        this.context = context;
    }


    public void getCommentsFromDB(final String searchQuery, final IDatabaseOperationsFetchComments callback) {

        final String Comments_object = null;

        StringRequest arrayRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_FETCH_COMMENT,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        final ArrayList<CommentsClass> comments_list = new ArrayList<>();
                        CommentsClass comment = null;

                        try {
                            JSONArray comments_arr = new JSONArray(response);

                            if(comments_arr.length() > 0) {
                                for (int i = 0; i < comments_arr.length(); i++) {

                                    try {
                                        JSONObject comments_obj = comments_arr.getJSONObject(i);
                                        comment = new CommentsClass(comments_obj.getInt("user_id"),
                                                comments_obj.getInt("food_id"),
                                                comments_obj.getInt("comment_id"),
                                                comments_obj.getString("comments"),
                                                comments_obj.getString("date"),
                                                comments_obj.getString("location"),
                                                comments_obj.getString("username"),
                                                comments_obj.getString("user_rating"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    comments_list.add(comment);
                                }

                                callback.onSuccess(false, comments_list);
                            }else{
                                callback.onSuccess(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("searchQuery", searchQuery);
                return params;
            }

        };

        RequestHandler.getInstance(context).addToRequestQueue(arrayRequest);

    }

}

