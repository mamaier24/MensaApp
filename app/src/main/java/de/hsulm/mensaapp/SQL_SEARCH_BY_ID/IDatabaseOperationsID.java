package de.hsulm.mensaapp.SQL_SEARCH_BY_ID;

import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.FoodClass;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for callback if server response is successful
 */
public interface IDatabaseOperationsID {
    void onSuccess(FoodClass food);
}
