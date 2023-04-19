package com.vdev.bookingevent.presenter;


import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;
import com.vdev.bookingevent.model.User;

import java.util.List;

public class DashboardMonthPresenter implements DashboardMonthContract.Presenter{
    private DashboardMonthContract.View view;
    private MConvertTime mConvertTime;

    private FirebaseController fc;

    public DashboardMonthPresenter(DashboardMonthContract.View view , MConvertTime mConvertTime, FirebaseController fc) {
        this.view = view;
        this.mConvertTime = mConvertTime;
        this.fc = fc;
    }


    @Override
    public Event findEventInData(int idEvent) {
        for(int i=0 ; i < MData.arrEvent.size() ; i++){
            Event tempEvent = MData.arrEvent.get(i);
            if(tempEvent.getId() == idEvent){
                return tempEvent;
            }
        }
        return null;
    }

    @Override
    public String convertTimeToStringDE(long dateStart, long dateEnd) {
        String timeStart = mConvertTime.convertDateToString1(mConvertTime.convertMiliToDate(dateStart));
        String timeEnd = mConvertTime.convertDateToString1(mConvertTime.convertMiliToDate(dateEnd));
        return timeStart + "\n" + timeEnd;
    }

    @Override
    public String getNameRoom(int room_id) {
        for(int i=0 ; i<MData.arrRoom.size() ; i++){
            Room room = MData.arrRoom.get(i);
            if(room.getId() == room_id){
                return room.getName();
            }
        }
        return null;
    }
}
