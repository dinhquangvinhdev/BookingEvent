package com.vdev.bookingevent.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class MConvertTime {
    SimpleDateFormat sdf1;
    SimpleDateFormat sdf2;
    SimpleDateFormat sdf3;

    public MConvertTime() {
        sdf1 = new SimpleDateFormat("HH:mm");
        sdf2 = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    public Date convertMiliToDate(long miliseconds){
        return new Date(miliseconds);
    }

    public Calendar convertMiliToCalendar(long miliseconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(miliseconds));
        return calendar;
    }

    public long convertStringToMili(String date){
        try {
            Date tempDate = sdf2.parse(date);
            return convertDateToMili(tempDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public long convertDateToMili(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * return HH-mm
     * @param date
     * @return
     */
    public String convertDateToString(Date date){
        String result = sdf1.format(date);
        return result;
    }

    /**
     * return dd/MM/yyyy hh:mm
     * @param date
     * @return
     */
    public String convertDateToString1(Date date){
        String result = sdf3.format(date);
        return result;
    }
}
