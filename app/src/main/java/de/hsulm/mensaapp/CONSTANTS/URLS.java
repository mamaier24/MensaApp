package de.hsulm.mensaapp.CONSTANTS;

/**
 * Created by Marcel Maier on 30/11/18.
 *
 * Defines all constants: In this case all URLs needed for PHP handling.
 */
public class URLS {

    //ROOT URLs
    public static final String ROOT_URL = "http://www.s673993392.online.de/v1/";
    public static final String ROOT_URL_PICTURES = "http://www.s673993392.online.de/v1/upload/";

    //URLs needed for login and register
    public static final String URL_REGISTER = ROOT_URL + "user_register.php";
    public static final String URL_LOGIN = ROOT_URL + "user_login.php";

    //URLs needed for searching
    public static final String URL_SEARCH_BY_ID = ROOT_URL + "search_by_id.php";
    public static final String URL_SEARCH_BY_FRAGMENTS = ROOT_URL + "search_by_fragments.php";

    //URLs needed for transmission
    public static final String URL_TRANSMIT_RATING = ROOT_URL + "transmit_user_rating.php";
    public static final String URL_TRANSMIT_COMMENT = ROOT_URL + "transmit_comment.php";
    public static final String URL_TRANSMIT_IMG = ROOT_URL + "transmit_img.php";

    //URLs needed for fetching
    public static final String URL_FETCH_RATING = ROOT_URL + "fetch_user_rating.php";
    public static final String URL_FETCH_IMG = ROOT_URL + "fetch_img.php";
    public static final String URL_FETCH_COMMENT = ROOT_URL + "fetch_comments.php";

}
