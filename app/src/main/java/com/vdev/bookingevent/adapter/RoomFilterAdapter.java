package com.vdev.bookingevent.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.databinding.ItemDialogFilterBinding;
import com.vdev.bookingevent.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoomFilterAdapter extends RecyclerView.Adapter<RoomFilterAdapter.ViewHolder> {

    private List<Boolean> choiced;

    public RoomFilterAdapter(List<Boolean> choiced) {
        //sample data choice
        setChoiced(choiced);
    }

    @NonNull
    @Override
    public RoomFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemDialogFilterBinding.inflate(LayoutInflater.from(parent.getContext()), parent ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoomFilterAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return MData.arrRoom.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private  ItemDialogFilterBinding binding;

        public ViewHolder(@NonNull ItemDialogFilterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position){
            Room room = MData.arrRoom.get(position);
            binding.tvRoomName.setText(room.getName());
            binding.imgRoomColor.clearColorFilter();
            binding.imgRoomColor.setBackgroundColor(Color.parseColor(room.getColor()));

            binding.cbRoomFilter.setChecked(choiced.get(position));

            binding.cbRoomFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choiced.set(position, !choiced.get(position));
                }
            });
        }
    }

    public List<Boolean> getChoiced() {
        return choiced;
    }

    public void setChoiced(List<Boolean> choiced) {
        if(choiced == null){
            this.choiced = new ArrayList<>(Arrays.asList(new Boolean[20]));
            Collections.fill(this.choiced , false);
        } else {
            this.choiced = new ArrayList<>(choiced);
        }
        notifyDataSetChanged();
    }

    public void clearAllChoiced(){
        Collections.fill(choiced, false);
        notifyDataSetChanged();
    }
}
