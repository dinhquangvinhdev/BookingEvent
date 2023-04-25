package com.vdev.bookingevent.adapter;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.callback.OnItemEventOverlap;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.databinding.ItemEventOverlapBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.presenter.EditEventContract;

import java.util.Calendar;
import java.util.List;

public class EventsOverlapAdapter extends RecyclerView.Adapter<EventsOverlapAdapter.MyViewHolder>{

    private MConvertTime mConvertTime;
    private List<Event> eventsOverlap;
    private TimePickerDialog tpd;
    private Context context;
    private final int TYPE_TIME_START_PICKER = 0;
    private final int TYPE_TIME_END_PICKER = 1;
    private OnItemEventOverlap callback;

    public EventsOverlapAdapter(Context context , List<Event> eventsOverlap , OnItemEventOverlap callback) {
        this.eventsOverlap = eventsOverlap;
        this.context = context;
        this.callback = callback;
        mConvertTime = new MConvertTime();
    }

    @NonNull
    @Override
    public EventsOverlapAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventOverlapBinding binding = ItemEventOverlapBinding.inflate(LayoutInflater.from(parent.getContext()), parent ,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsOverlapAdapter.MyViewHolder holder, int position) {
        holder.bind(eventsOverlap.get(position));
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

        public void bind(Event event){
            //TODO set host name
            Calendar calendarStart = mConvertTime.convertMiliToCalendar(event.getDateStart());
            Calendar calendarEnd = mConvertTime.convertMiliToCalendar(event.getDateEnd());
            String timeStart = "Time start: " + calendarStart.get(Calendar.HOUR_OF_DAY) + calendarStart.get(Calendar.MINUTE);
            String timeEnd = "Time end: " + calendarEnd.get(Calendar.HOUR_OF_DAY) + calendarEnd.get(Calendar.MINUTE);
            String hostName = "Host name: ";
            String titleEvent = "Title Event: " + event.getTitle();
            binding.tvHostName.setText(hostName);
            binding.tvTitleEvent.setText(titleEvent);
            binding.tvTimeStart.setText(timeStart);
            binding.tvTimeEnd.setText(timeEnd);
//            binding.tvTimeStart.setOnClickListener(it -> {showTimePicker(calendarStart , TYPE_TIME_START_PICKER);});
//            binding.tvTimeEnd.setOnClickListener(it -> {showTimePicker(calendarEnd , TYPE_TIME_END_PICKER);});
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
                            binding.tvTimeStart.setText("Time start: " + time);
                            break;
                        case TYPE_TIME_END_PICKER:
                            binding.tvTimeEnd.setText("Time end: " + time);
                            break;
                        default:
                            break;
                    }
                }
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);

            tpd.show();
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == imgDelete.getId()){
                callback.OnItemCLickListener(getBindingAdapterPosition());
            }
        }
    }
}