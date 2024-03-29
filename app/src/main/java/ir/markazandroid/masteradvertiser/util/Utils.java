package ir.markazandroid.masteradvertiser.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

import ir.markazandroid.masteradvertiser.util.date.GregorianDaysOfWeek;
import ir.markazandroid.masteradvertiser.util.date.PersianDaysOfWeek;
import ir.markazandroid.masteradvertiser.util.date.Roozh;


/**
 * Coded by Ali on 05/11/2017.
 */

public class Utils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String getNowForArduino(){
        Calendar calendar = Calendar.getInstance();
        //T:HH:MM:SS:DD:MM:YY:HH:MM:HH:MM#
        return String.format(Locale.US,"T:%d:%d:%d:%d:%d:%d:",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.YEAR));
    }

    public static void fade(View toVisibale, final View toFade, int duration) {
        toVisibale.setAlpha(0f);
        toVisibale.setVisibility(View.VISIBLE);
        toVisibale.animate().alpha(1f)
                .setDuration(duration)
                .setListener(null);
        toFade.setAlpha(1f);
        toFade.animate().alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toFade.setVisibility(View.GONE);
                    }
                });

    }

    public static String buildQuery(Bundle options) {
        StringBuilder builder = new StringBuilder();
        for (String key : options.keySet()) {
            builder.append(key)
                    .append("=")
                    .append(options.getInt(key))
                    .append("&");
        }
        if (builder.length() > 1) builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String timeStampToPastString(long unixTimeStamp) {
        int minutes = (int) ((System.currentTimeMillis() - unixTimeStamp) / (1000 * 60));
        int hours = minutes / 60;
        int days = hours / 24;
        int months = days / 30;
        if (months > 0) return Roozh.gregorianToPersian(unixTimeStamp);
        if (days > 0) return days + " روز پیش";
        if (hours > 0) return hours + " ساعت پیش";
        if (minutes > 0) return minutes + " دقیقه پیش";
        return "لحظاتی پیش";
    }

    public static String getPersianDateString(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String day = PersianDaysOfWeek.values()[calendar.get(Calendar.DAY_OF_WEEK)-1].toString();
        return day+" "+Roozh.gregorianToPersian(timestamp).replace('-','/');
    }

    public static String getDateString(long timestamp, String calendarType){
        switch (calendarType){
            case "persian": return getPersianDateString(timestamp);
            case "gregorian": return getGregorianDateString(timestamp);
            default: return "NA";
        }
    }

    public static String getGregorianDateString(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String day = GregorianDaysOfWeek.values()[calendar.get(Calendar.DAY_OF_WEEK)-1].toString();
        return String.format(Locale.getDefault(),"%s %04d/%02d/%02d",day,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static void fadeFade(final View toFade, int duration,boolean gone) {
        toFade.animate().alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toFade.setVisibility(gone?View.GONE:View.INVISIBLE);
                    }
                });

    }

    public static void fadeVisible(View toVisibale, int duration) {
        toVisibale.setAlpha(0f);
        toVisibale.setVisibility(View.VISIBLE);
        toVisibale.animate().alpha(1f)
                .setDuration(duration)
                .setListener(null);
        /*toFade.animate().alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toFade.setVisibility(View.GONE);
                    }
                });*/

    }

}
