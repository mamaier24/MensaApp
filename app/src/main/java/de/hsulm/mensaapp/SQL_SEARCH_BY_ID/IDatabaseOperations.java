package de.hsulm.mensaapp.SQL_SEARCH_BY_ID;

import java.util.ArrayList;
import de.hsulm.mensaapp.FoodClass;

public interface IDatabaseOperations {

    void onSuccess(ArrayList<FoodClass> food_list);

}
