package com.vdev.bookingevent.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vdev.bookingevent.databinding.ItemGuestEventDetailBinding;
import com.vdev.bookingevent.model.User;

import java.util.ArrayList;
import java.util.List;

public class GuestEventDetailAdapter extends RecyclerView.Adapter<GuestEventDetailAdapter.ViewHolder>{
    private List<User> participantData;
    private User host;

    public GuestEventDetailAdapter(List<User> guestData, User host) {
        this.participantData = new ArrayList<>(guestData);
        this.host = host;
        this.participantData.add(0, this.host);
    }

    @NonNull
    @Override
    public GuestEventDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGuestEventDetailBinding binding = ItemGuestEventDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestEventDetailAdapter.ViewHolder holder, int position) {
        if(position == 0 ){
            holder.bindHost(host);
        }else {
            holder.bindGuest(participantData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return participantData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemGuestEventDetailBinding binding;

        public ViewHolder(@NonNull ItemGuestEventDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindGuest(User guest){
            binding.tvNameGuest.setText(guest.getFullName());
            if(guest.getAvatar() != null && !guest.getAvatar().isEmpty()){
                Picasso.get().load(guest.getAvatar()).into(binding.imgAvatarGuest);
            } else {
                binding.imgAvatarGuest.setBackgroundColor(Color.parseColor("#000000"));
            }

        }

        public void bindHost(User host){
            binding.tvNameGuest.setText(host.getFullName() + "\n" + "Organizer");
            binding.tvNameGuest.setTypeface(binding.tvNameGuest.getTypeface(), Typeface.BOLD);
            if(host.getAvatar() != null && !host.getAvatar().isEmpty()){
                Picasso.get().load(host.getAvatar()).into(binding.imgAvatarGuest);
            } else {
                binding.imgAvatarGuest.setBackgroundColor(Color.parseColor("#000000"));
            }

        }
    }

    public void updateDataGuest(List<User> guests, User host){
        this.participantData.clear();
        this.participantData = new ArrayList<>(guests);
        this.host = host;
        this.participantData.add(0, this.host);
        notifyDataSetChanged();
    }
}
