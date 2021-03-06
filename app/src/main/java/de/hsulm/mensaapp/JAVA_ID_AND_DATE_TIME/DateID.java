package de.hsulm.mensaapp.JAVA_ID_AND_DATE_TIME;

import android.icu.util.Calendar;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Stephan Danz 05/12/2018
 */
public class DateID {

    public String getFoodID() {

        String day_str ="";
        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        int weekNumber = calDe.get(Calendar.WEEK_OF_YEAR);
        int year = calDe.get(Calendar.YEAR);
        int day = calDe.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                day_str="FR";
                break;
            case Calendar.MONDAY:
                day_str="MO";
                break;
            case Calendar.TUESDAY:
                day_str="DI";
                break;
            case Calendar.WEDNESDAY:
                day_str="MI";
                break;
            case Calendar.THURSDAY:
                day_str="DO";
                break;
            case Calendar.FRIDAY:
                day_str="FR";
                break;
            case Calendar.SATURDAY:
                day_str="FR";
                break;
        }


        if(weekNumber<10) {
            String food_id = "Y" + year + ":CW0" + weekNumber + ":" + day_str;
            return (food_id);
        }else{
            String food_id = "Y" + year + ":CW" + weekNumber + ":" + day_str;
            return (food_id);
        }
    }


    public String getDay(){

        String day_str ="";
        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        int day = calDe.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                day_str="Freitag";
                break;
            case Calendar.MONDAY:
                day_str="Montag";
                break;
            case Calendar.TUESDAY:
                day_str="Dienstag";
                break;
            case Calendar.WEDNESDAY:
                day_str="Mittwoch";
                break;
            case Calendar.THURSDAY:
                day_str="Donnerstag";
                break;
            case Calendar.FRIDAY:
                day_str="Freitag";
                break;
            case Calendar.SATURDAY:
                day_str="Freitag";
                break;
        }

        return day_str;

    }


    public  String returnYear(){

        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        String year = Integer.toString(calDe.get(Calendar.YEAR));

        return year;

    }


    public  String returnWeek(){

        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        String week = Integer.toString(calDe.get(Calendar.WEEK_OF_YEAR));

        return week;

    }


    public String getDate() {

        String date ="";
        Calendar calDe = Calendar.getInstance(Locale.GERMAN);
        calDe.setTime(new Date());
        int month = calDe.get(Calendar.MONTH);
        int year = calDe.get(Calendar.YEAR);
        int day = calDe.get(Calendar.DAY_OF_MONTH);

        String[] german_months = {"Januar", "Februar", "März", "April", "Mai", "Juni",
                "Juli", "August","September", "Oktober", "November", "Dezember"};

        date =  day + "." + " " + german_months[month] + " " + year;
        return (date);

    }

}
