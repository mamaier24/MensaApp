package de.hsulm.mensaapp.SQL_Download_Comments;

import java.util.ArrayList;
import de.hsulm.mensaapp.CommentsClass;

public interface IDatabaseOperationsComments {

    void onSuccess(ArrayList<CommentsClass> comments_list);

}
