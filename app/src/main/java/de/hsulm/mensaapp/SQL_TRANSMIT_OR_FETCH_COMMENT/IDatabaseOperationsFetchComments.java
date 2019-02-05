package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT;

import java.util.ArrayList;
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.CommentsClass;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for callback if server response is successful
 */
public interface IDatabaseOperationsFetchComments {

    void onSuccess(boolean isDefault);

    void onSuccess(boolean isDefault, ArrayList<CommentsClass> comments_list);

}
