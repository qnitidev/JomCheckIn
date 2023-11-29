package com.mietrix.myqruum;

public class Config {

    public static final String SHARED_PREF_NAME = "myQniti";

    public static final String URL_API = "https://myqr.qniti.com/apps/";


    public static final String SCAN_DATE = "scanDate";
    public static final String SCAN_TIME = "scanTime";
    public static final String EXIT_DATE = "exitDate";
    public static final String EXIT_TIME = "exitTime";

    //For User
    public static final String USER_ID2 = "userID2";
    public static final String NAME_ID2 = "nameID2";
    public static final String PHONE_ID2 = "phoneID2";
    public static final String EMAIL_ID2 = "emailID2";
    public static final String LOG_ID2 = "logID2";
    public static final String PLACE_ID = "placeID";
    public static final String ADDRESS_ID2 = "useraddress";
    public static final String LOG_STATUS = "logStatus";


    //Keys for email and password as defined in our $_POST['key'] in login.php
//public static final String KEY_ID = "userIC";
    public static final String KEY_PASSWORD = "userpass";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //This would be used to store the phone number of current logged in user
    public static final String ID_SHARED_PREF = "userID";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}
