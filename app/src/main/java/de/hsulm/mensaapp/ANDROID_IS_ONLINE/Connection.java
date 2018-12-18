package de.hsulm.mensaapp.ANDROID_IS_ONLINE;

import android.content.Context;
import android.net.ConnectivityManager;

public class Connection {

    private static final Connection ourInstance = new Connection();

    public static Connection getInstance() {
        return ourInstance;
    }

    private Connection() {

    }

    public boolean isOnline(Context context) {
        boolean var = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( cm.getActiveNetworkInfo() != null ) {
            var = true;
        }
        return var;
    }

}
