package com.vdev.bookingevent.presenter;

import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;

import java.time.LocalDate;
import java.util.List;

public interface DashboardMonthContract {
    interface Presenter{
        Event findEventInData(int idEvent);

        String convertTimeToStringDE(long dateStart, long dateEnd);

        String getNameRoom(int room_id);

        void filterEvents( List<Event> events, long startTime , long endTime);

        void updateFilterEvent(LocalDate selectedDay, List<Event> events);
    }
    interface View{}
}
