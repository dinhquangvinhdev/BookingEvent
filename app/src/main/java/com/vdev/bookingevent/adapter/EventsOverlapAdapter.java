package com.vdev.bookingevent.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdev.bookingevent.callback.OnItemEventOverlap;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.ItemEventOverlapBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventsOverlapAdapter extends RecyclerView.Adapter<EventsOverlapAdapter.MyViewHolder>{

    private MConvertTime mConvertTime;
    private List<Event> eventsOverlap;
    private List<User> hosts;
    private TimePickerDialog tpd;
    private Context context;
    private final int TYPE_TIME_START_PICKER = 0;
    private final int TYPE_TIME_END_PICKER = 1;
    private OnItemEventOverlap callback;
    private FirebaseController fc;
    private String tempStartTime;
    private String tempEndTime;

    public EventsOverlapAdapter(Context context , List<Event> eventsOverlap , List<User> hosts, OnItemEventOverlap callback) {
        this.eventsOverlap = eventsOverlap;
        this.context = context;
        this.callback = callback;
        this.hosts = hosts;
        mConvertTime = new MConvertTime();
        fc = new FirebaseController(null,null,null, null, null);
    }

    @NonNull
    @Override
    public EventsOverlapAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventOverlapBinding binding = ItemEventOverlapBinding.inflate(LayoutInflater.from(parent.getContext()), parent ,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsOverlapAdapter.MyViewHolder holder, int position) {
        holder.bind(eventsOverlap.get(position), hosts.get(position));
    }

    @Override
    public int getItemCount() {
        return eventsOverlap.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemEventOverlapBinding binding;
        private ImageView imgDelete;

        public MyViewHolder(@NonNull ItemEventOverlapBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            imgDelete = binding.imgDeleteEvent;
        }

        public void bind(Event event, User host){
            Calendar calendarStart = mConvertTime.convertMiliToCalendar(event.getDateStart());
            Calendar calendarEnd = mConvertTime.convertMiliToCalendar(event.getDateEnd());
            String timeStart = String.format(MConst.FORMAT_TIME, calendarStart.get(Calendar.HOUR_OF_DAY)  , calendarStart.get(Calendar.MINUTE));
            String timeEnd = String.format(MConst.FORMAT_TIME, calendarEnd.get(Calendar.HOUR_OF_DAY)  , calendarEnd.get(Calendar.MINUTE));
            String hostName = "Host name: " + host.getFullName();
            String titleEvent = "Title Event: " + event.getTitle();
            binding.tvHostName.setText(hostName);
            binding.tvTitleEvent.setText(titleEvent);
            binding.tvTime.setText("Time: " + timeStart + " - " + timeEnd);
            int checkPriority = fc.comparePriorityUser(host.getId());
            if( checkPriority == 1){
                binding.imgDeleteEvent.setVisibility(View.INVISIBLE);
                binding.imgEditEvent.setVisibility(View.INVISIBLE);
            } else if(checkPriority == 0 ){
                binding.imgDeleteEvent.setVisibility(View.INVISIBLE);
                binding.imgEditEvent.setVisibility(View.VISIBLE);
            } else if(checkPriority == 2 || checkPriority == 3){
                binding.imgDeleteEvent.setVisibility(View.VISIBLE);
                binding.imgEditEvent.setVisibility(View.VISIBLE);
            } else {
                //something bad when compare event
                binding.imgDeleteEvent.setVisibility(View.INVISIBLE);
                binding.imgEditEvent.setVisibility(View.INVISIBLE);
            }
            binding.imgEditEvent.setOnClickListener(it -> {
                showTimePicker(calendarEnd,TYPE_TIME_END_PICKER);
                showTimePicker(calendarStart, TYPE_TIME_START_PICKER);
            });
            binding.imgDeleteEvent.setOnClickListener(this);
        }

        private void showTimePicker(Calendar calendar, int type) {
            tpd = new TimePickerDialog(context, android.R.style.Theme_Holo_Dialog_MinWidth
                    ,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    String time = String.format(MConst.FORMAT_TIME, hour , minute);
                    switch (type){
                        case TYPE_TIME_START_PICKER:
                            tempStartTime = time;
                            break;
                        case TYPE_TIME_END_PICKER:
                            tempEndTime = time;
                            if(compareTimeDateStartAndDateEnd(tempStartTime, tempEndTime)){
                                Event editEvent = eventsOverlap.get(getBindingAdapterPosition());
                                Date dateStartEvent = mConvertTime.convertMiliToDate(editEvent.getDateStart());
                                String date = mConvertTime.convertDateToString3(dateStartEvent);
                                editEvent.setDateStart(mConvertTime.convertStringToMili(tempStartTime+ " " + date));
                                editEvent.setDateEnd(mConvertTime.convertStringToMili(tempEndTime+ " " + date));
                                editEvent.setDateUpdated(System.currentTimeMillis());
                                callback.OnItemEditCLickListener(editEvent);
                            }else {
                                Log.d("bibibla", "can not edit");
                            }
                            break;
                        default:
                            break;
                    }
                }
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);

            tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    switch (type){
                        case TYPE_TIME_START_PICKER:
                            tempStartTime = null;
                            break;
                        case TYPE_TIME_END_PICKER:
                            tempEndTime = null;
                            break;
                        default:
                            break;
                    }
                }
            });

            //set title for timePicker
            if(type == TYPE_TIME_START_PICKER){
                tpd.setTitle("Time Start Event");
            } else if(type == TYPE_TIME_END_PICKER) {
                tpd.setTitle("Time End Event");
            }
            tpd.show();
        }

        private boolean compareTimeDateStartAndDateEnd(String startTime , String endTime) {
            if(startTime == null || endTime == null){
                return false;
            }
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            Duration duration = Duration.between(start , end);
            if(duration.isNegative() || duration.isZero()){
                return false;
            }
            return true;
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == imgDelete.getId()){
                callback.OnItemDeleteCLickListener(getBindingAdapterPosition());
            }
        }
    }
}
