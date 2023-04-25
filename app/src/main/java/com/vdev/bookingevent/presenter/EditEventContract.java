package com.vdev.bookingevent.presenter;

public interface EditEventContract {
    interface View{}
    interface Presenter{
        int findIndexRoomOfEvent(int roomId);

        boolean compareTimeDateStartAndDateEnd(String StartTime , String EndTime);
    }
}
