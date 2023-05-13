package com.vdev.bookingevent.presenter;

import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.model.Room;

import java.time.Duration;
import java.time.LocalTime;

public class EditEventPresenter implements EditEventContract.Presenter{
    private EditEventContract.View view;

    public EditEventPresenter(EditEventContract.View view) {
        this.view = view;
    }

    @Override
    public int findIndexRoomOfEvent(int roomId) {
        for(int i=0 ; i< MData.arrRoom.size() ; i++){
            if(MData.arrRoom.get(i).getId() == roomId){
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean compareTimeDateStartAndDateEnd(String StartTime, String EndTime) {
        LocalTime start = LocalTime.parse(StartTime);
        LocalTime end = LocalTime.parse(EndTime);
        Duration duration = Duration.between(start , end);
        if(duration.isNegative() || duration.isZero()){
            return false;
        }
        return true;
    }
}
