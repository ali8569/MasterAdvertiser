package ir.markazandroid.masteradvertiser.network;


/**
 * Coded by Ali on 05/11/2017.
 */

public abstract class NetStatics {


    // chnage local address to public
    //static final String DOMAIN = "http://harajgram.ir";
    //static final String DOMAIN = "http://192.168.1.36:8080";
    public static final String DOMAIN = "http://192.168.1.3:8080";
    //static final String SUFFIX = DOMAIN + "/uniengine/";
    static final String SUFFIX = DOMAIN + "/deviceApi";


    static final String REGISTRATION=SUFFIX+"/registration";
    static final String REGISTRATION_REGISTER=REGISTRATION+"/register";
    static final String REGISTRATION_LOGIN=REGISTRATION+"/login";

    static final String RECORD=SUFFIX+"/record";

    static final String PHONE=SUFFIX+"/phone";
    static final String PHONE_FIRSTLOGIN=PHONE+"/firstLogin";
    public static final String VERSION=PHONE+"/getUpdate";

    static final String DEVICE_CAMPAIGN=SUFFIX+"/campaign";
    static final String DEVICE_CAMPAIGN_GET=DEVICE_CAMPAIGN+"/get";

}
