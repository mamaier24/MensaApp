package de.hsulm.mensaapp.SQL_UPLOAD_OR_FETCH_IMAGE;

import java.util.ArrayList;

public interface IDatabaseOperationsFetchImages {
    void onSuccess(ArrayList<String> img_id_list);
}
