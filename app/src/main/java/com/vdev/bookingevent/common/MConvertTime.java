package com.vdev.bookingevent.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MConvertTime {
    SimpleDateFormat sdf1;

    public MConvertTime() {
        sdf1 = new SimpleDateFormat("HH:mm");
    }

    public Date convertMiliToDate(long miliseconds){
        return new Date(miliseconds);
    }

    /**
     * return hh-mm
     * @param date
     * @return
     */
    public String convertDateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm");
        String result = sdf1.format(date);
        return result;
    }
}
