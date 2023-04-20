package com.vdev.bookingevent.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class MConvertTime {
    SimpleDateFormat sdf1;
    SimpleDateFormat sdf2;
    SimpleDateFormat sdf3;
    SimpleDateFormat sdf4;

    public MConvertTime() {
        sdf1 = new SimpleDateFormat("HH:mm");
        sdf2 = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf4 = new SimpleDateFormat("dd-MM-yyyy");
    }

    public Date convertMiliToDate(long miliseconds){
        return new Date(miliseconds);
    }

    public Calendar convertMiliToCalendar(long miliseconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(miliseconds));
        return calendar;
    }

    /**
     * convert string HH:mm dd-MM-yyyy
     * @param date
     * @return
     */
    public long convertStringToMili(String date){
        try {
            Date tempDate = sdf2.parse(date);
            return convertDateToMili(tempDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public long getMiliStartDayFromLocalDate(LocalDate localDate){
        LocalDateTime startLDT = localDate.atTime(0, 0, 0, 0);
        ZonedDateTime startZDT = ZonedDateTime.of(startLDT, ZoneId.systemDefault());
        long startTime = startZDT.toInstant().toEpochMilli();
        return startTime;
    }

    public long getMiliLastDayFromLocalDate(LocalDate localDate){
        LocalDateTime startLDT = localDate.atTime(23, 59, 59, 0);
        ZonedDateTime startZDT = ZonedDateTime.of(startLDT, ZoneId.systemDefault());
        long lastTime = startZDT.toInstant().toEpochMilli();
        return lastTime;
    }

    public LocalDate convertMiliToLocalDate(long time){
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
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

    /**
     * return dd-MM-yyyy
     * @param date
     * @return
     */
    public String convertDateToString3(Date date){
        String result = sdf4.format(date);
        return result;
    }
}
