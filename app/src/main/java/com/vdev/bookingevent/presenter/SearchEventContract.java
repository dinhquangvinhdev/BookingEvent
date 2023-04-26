package com.vdev.bookingevent.presenter;

import com.vdev.bookingevent.model.Event;

public interface SearchEventContract {
    interface View{

    }

    interface Presenter{

        void searchEvents(String title, int roomId, String startDate, String endDate);

        Event findEventInData(int idEvent);

        String convertTimeToStringDE(long dateStart, long dateEnd);

        String getNameRoom(int room_id);
    }
}
