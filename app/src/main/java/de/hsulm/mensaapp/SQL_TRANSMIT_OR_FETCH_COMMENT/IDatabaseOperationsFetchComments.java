package de.hsulm.mensaapp.SQL_TRANSMIT_OR_FETCH_COMMENT;

import java.util.ArrayList;
import de.hsulm.mensaapp.CLASS_OBJ.CommentsClass;

public interface IDatabaseOperationsFetchComments {

    void onSuccess(boolean isDefault);

    void onSuccess(boolean isDefault, ArrayList<CommentsClass> comments_list);

}
