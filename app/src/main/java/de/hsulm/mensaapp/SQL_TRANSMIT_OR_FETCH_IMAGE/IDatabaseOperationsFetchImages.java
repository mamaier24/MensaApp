package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_IMAGE;

import java.util.ArrayList;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for callback if server response is successful
 */
public interface IDatabaseOperationsFetchImages {
    void onSuccess(ArrayList<String> img_id_list);
}
