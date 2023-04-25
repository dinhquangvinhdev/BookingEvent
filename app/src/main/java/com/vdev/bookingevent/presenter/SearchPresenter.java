package com.vdev.bookingevent.presenter;

import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.model.Event;

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
        fc = new FirebaseController(callbackUpdateEventDisplay, null, null);
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
}
