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

        long getMiliFirstDayChoiceCal(LocalDate selectedDay);

        long getMiliLastDayChoiceCal(LocalDate selectedDay);

        void filterEvents(long startTime , long endTime);

        void updateFilterEvent(LocalDate selectedDay);
    }
    interface View{}
}
