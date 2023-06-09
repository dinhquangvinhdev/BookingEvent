package com.vdev.bookingevent.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdev.bookingevent.callback.CallbackItemCalDashMonth;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.databinding.ItemDashMonthBinding;
import com.vdev.bookingevent.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventsDashMonthAdapter extends RecyclerView.Adapter<EventsDashMonthAdapter.MyViewHolder> {

    private List<Event> events;
    private CallbackItemCalDashMonth callback;
    private MConvertTime mConvertTime;

    public EventsDashMonthAdapter(CallbackItemCalDashMonth callback) {
        this.events = new ArrayList<>();
        this.callback = callback;
        mConvertTime = new MConvertTime();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDashMonthBinding binding = ItemDashMonthBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ItemDashMonthBinding binding;

        public MyViewHolder(@NonNull ItemDashMonthBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event item) {
            binding.tvTitleItemDashMonth.setText(item.getTitle());
            String timeStart = mConvertTime.convertDateToString(mConvertTime.convertMiliToDate(item.getDateStart()));
            String timeEnd = mConvertTime.convertDateToString(mConvertTime.convertMiliToDate(item.getDateEnd()));
            binding.tvTime.setText(timeStart + " - " + timeEnd);
            //change color background
            binding.llItemDashMonth.getBackground().clearColorFilter();
            binding.llItemDashMonth
                    .getBackground().setColorFilter(Color.parseColor(item.getRoomColor()), PorterDuff.Mode.SRC);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.openSlidingPanel(item.getId(), item.getRoomColor());
                }
            });
        }
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
