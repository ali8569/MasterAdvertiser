package ir.markazandroid.masteradvertiser.util;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Coded by Ali on 09/01/2018.
 */

public class PreferencesManager {


    private static final String ARDUINO_TIME="ir.markazandroid.advertiser.util.PreferencesManager.ARDUINO_TIME";
    private static final String STARTUP_CMDS="ir.markazandroid.advertiser.util.PreferencesManager.STARTUP_CMDS";


    private SharedPreferences sharedPreferences;

    public PreferencesManager(SharedPreferences sharedPreferences){
        this.sharedPreferences=sharedPreferences;
    }

    public String getArduinoOnOffTime(){
        return sharedPreferences.getString(ARDUINO_TIME,null);
    }
    public void setArduinoOnOffTime(String onOffTime){
        sharedPreferences.edit().putString(ARDUINO_TIME,onOffTime).apply();
    }

    public Set<String> getStartupCommands(){
        return sharedPreferences.getStringSet(STARTUP_CMDS,new HashSet<>());
    }
    public synchronized void addStartupCommand(String command){
        Set<String> cmds = getStartupCommands();
        cmds.add(command);
        sharedPreferences.edit().putStringSet(STARTUP_CMDS,cmds).apply();
    }

    public synchronized void removeStartupCommand(String command){
        Set<String> cmds = new HashSet<>(getStartupCommands());
        cmds.remove(command);
        sharedPreferences.edit().putStringSet(STARTUP_CMDS,cmds).apply();
    }
}
