package de.hsulm.mensaapp.SQL_SEARCH_BY_DATEID;

import java.util.ArrayList;
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.FoodClass;

public interface IDatabaseOperationsDateID {
    void onSuccess(ArrayList<FoodClass> food_list);
}
