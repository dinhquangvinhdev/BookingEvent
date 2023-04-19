package com.vdev.bookingevent.presenter;

import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;

import java.util.List;

public class DashboardMonthContract {
    public interface Presenter{
        Event findEventInData(int idEvent);

        String convertTimeToStringDE(long dateStart, long dateEnd);

        String getNameRoom(int room_id);
    }
    public interface View{}
}
