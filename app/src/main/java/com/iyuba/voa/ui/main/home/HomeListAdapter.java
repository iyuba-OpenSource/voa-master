package com.iyuba.voa.ui.main.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.voa.R;
import com.iyuba.voa.databinding.ItemRvHomeBinding;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {
    List<HomeListItemViewModel> list;

    public HomeListAdapter(List<HomeListItemViewModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HomeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvHomeBinding inflate = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_rv_home
                , parent,
                false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListAdapter.MyViewHolder holder, int position) {
        HomeListItemViewModel user = list.get(position);
        MyViewHolder viewHolder = holder;
        viewHolder.itemBinding.setViewModel(user);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ItemRvHomeBinding itemBinding;

        public MyViewHolder(@NonNull ItemRvHomeBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
        }

    }
}
