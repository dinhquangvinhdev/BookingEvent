package com.vdev.bookingevent.adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.databinding.ItemOptionAccountBinding;

import java.util.List;

public class OptionAccountAdapter extends RecyclerView.Adapter<OptionAccountAdapter.ViewHolder> {

    private List<String> titleOptionAccount = MConst.titleOptionAccount;
    private OnItemOpAccClickListener listener;

    public OptionAccountAdapter(OnItemOpAccClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOptionAccountBinding binding = ItemOptionAccountBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionAccountAdapter.ViewHolder holder, int position) {
        holder.bind(titleOptionAccount.get(position));
    }

    @Override
    public int getItemCount() {
        return titleOptionAccount.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemOptionAccountBinding binding;

        public ViewHolder(@NonNull ItemOptionAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String title){
            switch (title){
                case "Logout": binding.tvTitleOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_logout, 0,0,0); break;
                case "Detail Account": binding.tvTitleOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_account_nchoice, 0,0,0); break;
                default: break;
            }
            binding.tvTitleOption.setText(title);
            binding.cvOptionAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemCLickListener(title);
                }
            });
        }
    }
}
