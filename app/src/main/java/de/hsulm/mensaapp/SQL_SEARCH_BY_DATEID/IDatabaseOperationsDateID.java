package de.hsulm.mensaapp.SQL_SEARCH_BY_DATEID;

import java.util.ArrayList;
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.FoodClass;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for callback if server response is successful
 */
public interface IDatabaseOperationsDateID {
    void onSuccess(ArrayList<FoodClass> food_list);
}
