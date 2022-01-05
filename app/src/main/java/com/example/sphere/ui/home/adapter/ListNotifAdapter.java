package com.example.sphere.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sphere.R;
import com.example.sphere.ui.home.model.Notif;
import com.example.sphere.ui.profile.model.MyReportList;
import com.example.sphere.util.DateFormatter;

import java.util.ArrayList;

public class ListNotifAdapter extends RecyclerView.Adapter<ListNotifAdapter.UserViewHolder> {
    private ArrayList<Notif> dataList;
    private Context context;

    public ListNotifAdapter(ArrayList<Notif> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_notif, parent, false);
        context = parent.getContext();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Notif items = dataList.get(position);
        holder.tvTitle.setText(items.getStatus());
        holder.tvMsg.setText(items.getMessage());
        holder.tvDate.setText(new DateFormatter().convertDate(items.getDate()));

        if (items.getStatus().equals("Aman")) {
            holder.iv.setImageResource(R.drawable.ic_safe);
        } else if (items.getStatus().equals("Siaga")) {
            holder.iv.setImageResource(R.drawable.ic_warning);
        } else if (items.getStatus().equals("Bahaya")) {
            holder.iv.setImageResource(R.drawable.ic_danger);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDate, tvMsg;
        private ImageView iv;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}
