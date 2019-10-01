package ir.markazandroid.masteradvertiser.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.stream.StreamSupport;

import ir.markazandroid.masteradvertiser.network.JSONParser.Parser;
import ir.markazandroid.masteradvertiser.object.Schedule;

/**
 * Coded by Ali on 9/26/2019.
 */
public class Cache {

    private final static String SCHEDULE_FILE_PATH="schedule";

    private Context context;
    private Parser parser;

    public Cache(Context context, Parser parser) {
        this.context = context;
        this.parser = parser;
    }

    public void cacheSchedule(Schedule schedule){
        try {
            FileOutputStream outputStream =context.openFileOutput(SCHEDULE_FILE_PATH,Context.MODE_PRIVATE);
            outputStream.write(parser.get(schedule).toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Schedule getCachedSchedule(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(SCHEDULE_FILE_PATH)));
            String s;
            StringBuilder builder = new StringBuilder();
            while ((s=reader.readLine())!=null){
                builder.append(s);
            }
            return parser.get(Schedule.class,new JSONObject(builder.toString()));
        } catch (IOException | JSONException e) {
            return null;
        }
    }
}
