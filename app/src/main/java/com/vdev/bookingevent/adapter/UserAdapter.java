package com.vdev.bookingevent.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdev.bookingevent.callback.OnItemUserClickListener;
import com.vdev.bookingevent.databinding.ItemGuestAddEventBinding;
import com.vdev.bookingevent.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> implements Filterable {
    private List<User> users;
    private List<User> listUserFull;
    private OnItemUserClickListener callback;
    private int type;

    public UserAdapter(List<User> users, OnItemUserClickListener callback , int type) {
        this.users = users;
        this.callback = callback;
        this.type = type;
        listUserFull = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGuestAddEventBinding binding = ItemGuestAddEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent , false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return filterUser;
    }

    private Filter filterUser = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(listUserFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(User user : listUserFull){
                    if(user.getFullName().toLowerCase().contains(filterPattern)){
                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            users.clear();
            users.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemGuestAddEventBinding binding;

        public MyViewHolder(@NonNull ItemGuestAddEventBinding itemGuestAddEventBinding) {
            super(itemGuestAddEventBinding.getRoot());
            binding = itemGuestAddEventBinding;
        }

        public void bind(User user){
            binding.tvNameGuest.setText(user.getFullName());
            binding.tvNameGuest.setOnClickListener(it -> {callback.OnItemUserCLickListener(user, type);});
        }
    }
}
