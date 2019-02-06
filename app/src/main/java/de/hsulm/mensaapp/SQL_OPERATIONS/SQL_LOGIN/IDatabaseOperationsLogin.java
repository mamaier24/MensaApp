package de.hsulm.mensaapp.SQL_OPERATIONS.SQL_LOGIN;

 /* Created by Stephan Danz 05/12/2018
 * Class necessary for callback if server response is successful
 */
public interface IDatabaseOperationsLogin {
    void onSuccess();
    void onError();
}
