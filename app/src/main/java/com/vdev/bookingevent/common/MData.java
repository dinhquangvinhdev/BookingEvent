package com.vdev.bookingevent.common;

import android.util.Log;

import com.vdev.bookingevent.model.Department;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MData {
    public static List<Event> arrEvent = new ArrayList<>();
    public static List<Room> arrRoom = new ArrayList<>();
    public static List<Event> arrFilterEvent = new ArrayList<>();
    public static List<Department> arrDepartment = new ArrayList<>();
    public static List<Boolean> filterChoicedRoom;
    public static int id_detail_participant = -1;   //save the last id detail participant in database
    public static int id_event = -1;                //save the last id event in database
    //TODO get the id_user when login
    public static int id_user = 0;                  //save the id user login
    public static long getStartMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public static long getEndMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

}
