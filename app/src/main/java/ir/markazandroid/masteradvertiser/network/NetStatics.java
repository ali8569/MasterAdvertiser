package ir.markazandroid.masteradvertiser.network;


/**
 * Coded by Ali on 05/11/2017.
 */

public abstract class NetStatics {


    // change local address to public
    public static final String DOMAIN = "http://harajgram.ir";
    //public static final String DOMAIN = "http://192.168.1.6:8080";
    //public static final String DOMAIN = "http://192.168.1.3:8080";
    public static final String SUFFIX = DOMAIN + "/uniengine";
    //public static final String SUFFIX = DOMAIN;

    static final String DEVICE_API = SUFFIX+"/deviceApi";

    static final String DEVICE = DEVICE_API+"/device";
    static final String DEVICE_GET_ME =DEVICE+"/getMe";


    static final String AUTHENTICATION =DEVICE_API+"/authentication";
    static final String REGISTRATION_REGISTER= AUTHENTICATION +"/register";
    static final String REGISTRATION_LOGIN= AUTHENTICATION +"/login";

    static final String RECORD=SUFFIX+"/record";

    static final String PHONE=SUFFIX+"/phone";
    static final String PHONE_FIRSTLOGIN=PHONE+"/firstLogin";
    public static final String VERSION=PHONE+"/getUpdate";

    static final String DEVICE_CAMPAIGN=DEVICE_API+"/campaign";
    static final String DEVICE_CAMPAIGN_GET=DEVICE_CAMPAIGN+"/get";

    static final String DEVICE_SCHEDULE=DEVICE_API+"/schedule";
    static final String DEVICE_SCHEDULE_GET=DEVICE_SCHEDULE+"/get";

}
