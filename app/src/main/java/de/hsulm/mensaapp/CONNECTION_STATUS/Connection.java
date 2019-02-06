package de.hsulm.mensaapp.CONNECTION_STATUS;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Stephan Danz on 30/11/18.
 * Checks online state of the mobile device
 */
public class Connection {

    private static final Connection ourInstance = new Connection();

    public static Connection getInstance() {
        return ourInstance;
    }

    private Connection() { }

    public boolean isOnline(Context context) {
        boolean var = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( cm.getActiveNetworkInfo() != null ) {
            var = true;
        }
        return var;
    }

}
