package com.vdev.bookingevent.presenter;

import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements SearchEventContract.Presenter{
    private SearchEventContract.View view;
    private FirebaseController fc;
    private List<Event> arrEventSearch;
    private List<String> tempRooms;
    private MConvertTime mConvertTime;

    public SearchPresenter(SearchEventContract.View view , CallbackUpdateEventDisplay callbackUpdateEventDisplay) {
        this.view = view;
        fc = new FirebaseController(callbackUpdateEventDisplay, null, null,null);
        //convert time
        mConvertTime = new MConvertTime();
        //event
        arrEventSearch = new ArrayList<>();
        //room
        tempRooms = new ArrayList<>();
        tempRooms.add("None");
        for(int i = 0; i< MData.arrRoom.size(); i++){
            tempRooms.add(MData.arrRoom.get(i).getNickName());
        }
    }

    public List<Event> getArrEventSearch() {
        return arrEventSearch;
    }

    public List<String> getTempRooms() {
        return tempRooms;
    }

    public MConvertTime getmConvertTime() {
        return mConvertTime;
    }

    @Override
    public void searchEvents(String title, int roomId, String startDate, String endDate) {
        if(roomId != -1){
            fc.getEventWithRoomId(title , roomId , startDate , endDate);
        } else if(startDate != ""){
            fc.getEventWithStartDate(title , startDate , endDate);
        } else if(endDate != ""){
            fc.getEventWithEndDate(title , endDate);
        } else {
            fc.getEventWithTitle(title);
        }
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

}
