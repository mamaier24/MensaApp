package de.hsulm.mensaapp.SQL_OPERATIONS.SQL_REQUEST_HANDLER;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Marcel Maier on 30/11/18.
 * Class which handles all HTTP/SQL requests with VOLLEY and queueing the requests in an orderly fashion
 */
public class RequestHandler {
    private static RequestHandler mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestHandler(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }


    public static synchronized RequestHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestHandler(context);
        }
        return mInstance;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
