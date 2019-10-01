package ir.markazandroid.masteradvertiser.util.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Coded by Ali on 9/24/2019.
 */
public class DateUtils {


    public static Date getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public static boolean belongsToToday(long timestamp){
        long diff =timestamp-getTodayDate().getTime();
        return diff>=0 && diff < 24*3600_000L;
    }
}
