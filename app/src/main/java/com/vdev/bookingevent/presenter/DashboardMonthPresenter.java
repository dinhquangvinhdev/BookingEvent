package com.vdev.bookingevent.presenter;


import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardMonthPresenter implements DashboardMonthContract.Presenter {
    private DashboardMonthContract.View view;
    private MConvertTime mConvertTime;

    private FirebaseController fc;

    public DashboardMonthPresenter(DashboardMonthContract.View view, MConvertTime mConvertTime, FirebaseController fc) {
        this.view = view;
        this.mConvertTime = mConvertTime;
        this.fc = fc;
    }


    @Override
    public Event findEventInData(int idEvent) {
        for (int i = 0; i < MData.arrEvent.size(); i++) {
            Event tempEvent = MData.arrEvent.get(i);
            if (tempEvent.getId() == idEvent && tempEvent.getStatus() == 0) {
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
        for (int i = 0; i < MData.arrRoom.size(); i++) {
            Room room = MData.arrRoom.get(i);
            if (room.getId() == room_id) {
                return room.getName();
            }
        }
        return null;
    }

    @Override
    public void filterEvents(List<Event> events, long startTime, long endTime) {
        if(events != null){         // this is will execute when call back receive the event filter with user
            updateUIWithFilterEvent(events,startTime, endTime);
        } else {
            //check filter with user
            if(MData.filterUser){
                fc.getEventsOfHost(MData.userLogin.getId());
            } else {
                updateUIWithFilterEvent(MData.arrEvent, startTime ,endTime);
            }
        }
    }

    private void updateUIWithFilterEvent(List<Event> arrEvent, long startTime, long endTime) {
        MData.arrFilterEvent.clear();
        for (int i = 0; i < arrEvent.size(); i++) {
            Event event = arrEvent.get(i);
            //the event have status is 0 is deleted (hide)
            if(event.getStatus() == 0){
                //filter time
                if (event.getDateStart() >= startTime && event.getDateEnd() <= endTime) {
                    //filter room choice
                    if(MData.filterChoicedRoom != null && MData.filterChoicedRoom.contains(true)) {
                        for (int j = 0; j < MData.filterChoicedRoom.size(); j++) {
                            if (MData.filterChoicedRoom.get(j)) {
                                int idRoom = MData.arrRoom.get(j).getId();
                                if (event.getRoom_id() == idRoom) {
                                    MData.arrFilterEvent.add(event);
                                    break;
                                }
                            }
                        }
                    } else {
                        //add all event if no filter from roomChoice
                        MData.arrFilterEvent.add(event);
                    }
                }
            }
        }
    }

    private List<Event> getEventsOfHost(int id) {
        List<Event> events = new ArrayList<>();

        return null;
    }

    @Override
    public void updateFilterEvent(LocalDate selectedDay, List<Event> events) {
        long startTime = mConvertTime.getMiliStartDayFromLocalDate(selectedDay);
        long endTime = mConvertTime.getMiliLastDayFromLocalDate(selectedDay);
        filterEvents(events, startTime , endTime);
    }
}
