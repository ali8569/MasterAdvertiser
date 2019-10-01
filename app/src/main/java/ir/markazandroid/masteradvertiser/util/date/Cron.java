package ir.markazandroid.masteradvertiser.util.date;

import android.content.Intent;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java8.util.function.Predicate;
import java8.util.stream.StreamSupport;

/**
 * Coded by Ali on 9/24/2019.
 */
public class Cron {

    private ArrayList<String> dayParams,monthParams;
    private String cron;


    public Cron(String cron){
        this.cron=cron;
        String[] crons = cron.split(" ");
        dayParams = new ArrayList<>();
        monthParams=new ArrayList<>();
        Collections.addAll(dayParams,crons[0].split(","));
        Collections.addAll(monthParams,crons[1].split(","));
    }


    public boolean isCronForAllDays(){
        return dayParams.get(0).equals("*");
    }

    public boolean isCronForAllMonths(){
        return monthParams.get(0).equals("*");
    }

    public boolean isCronWeekly(){
        try {
            Integer.parseInt(dayParams.get(0));
            return false;
        }catch (NumberFormatException e){
            return true;
        }
    }

    public boolean isCronDaily(){
        try {
            Integer.parseInt(dayParams.get(0));
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }


    public boolean matchesMonth(int month){
        return StreamSupport.stream(monthParams).anyMatch(s -> s.equalsIgnoreCase(month+""));
    }

    public boolean matchesDayOfWeek(String dayOfWeek){
        return StreamSupport.stream(dayParams).anyMatch(s -> s.equalsIgnoreCase(dayOfWeek));
    }

    public boolean matchesDayOfMonth(int dayOfMonth){
        return StreamSupport.stream(dayParams).anyMatch(s -> s.equalsIgnoreCase(dayOfMonth+""));
    }


    public boolean matchesMonthI(int month){
        return isCronForAllMonths() || matchesMonth(month);
    }

    public boolean matchesDayOfWeekI(String dayOfWeek){
        return isCronForAllDays() || matchesDayOfWeek(dayOfWeek);
    }

    public boolean matchesDayOfMonthI(int dayOfMonth){
        return isCronForAllDays() || matchesDayOfMonth(dayOfMonth);
    }



}
