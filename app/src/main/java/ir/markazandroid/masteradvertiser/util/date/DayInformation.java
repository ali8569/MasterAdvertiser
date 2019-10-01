package ir.markazandroid.masteradvertiser.util.date;

import java.util.Calendar;

/**
 * Coded by Ali on 9/24/2019.
 */
public class DayInformation {

    private String dayOfWeek;
    private int dayOfMonth;
    private int month;


    public DayInformation() {
        this(System.currentTimeMillis());
    }

    public DayInformation(long timestamp) {
        update(timestamp);
    }

    public void update(){
        update(System.currentTimeMillis());
    }

    public void update(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        dayOfWeek = SimpleDayOfWeek.values()[calendar.get(Calendar.DAY_OF_WEEK) - 1].name();
        int[] date = Roozh.gregorianToPersianSplited(timestamp);
        dayOfMonth = date[2];
        month = date[1];
    }


    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

}
