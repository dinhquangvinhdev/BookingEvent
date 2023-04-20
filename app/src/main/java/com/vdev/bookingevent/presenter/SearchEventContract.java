package com.vdev.bookingevent.presenter;

public interface SearchEventContract {
    interface View{

    }

    interface Presenter{

        void searchEvents(String title, int roomId, String startDate, String endDate);
    }
}
