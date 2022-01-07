package com.example.sphere.ui.patrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sphere.R;
import com.example.sphere.ui.patrol.model.Patrol;
import com.example.sphere.ui.profile.model.MyReportList;
import com.example.sphere.util.DateFormatter;

import java.util.ArrayList;

public class PatrolAdapter extends RecyclerView.Adapter<PatrolAdapter.UserViewHolder> {
    private ArrayList<Patrol> dataList;
    private Context context;

    public PatrolAdapter(ArrayList<Patrol> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_patrol, parent, false);
        context = parent.getContext();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Glide.with(context)
                .load(dataList.get(position).getPhoto())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.ivMaps);

        holder.tvTitle.setText(dataList.get(position).getName());
        holder.tvDate.setText(new DateFormatter().convertDate(dataList.get(position).getTask_date()));
        holder.tvDesc.setText(dataList.get(position).getDescription());

        holder.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.get(position).setExpandable(!dataList.get(position).getExpandable());
                notifyItemChanged(position);
            }
        });

        if(dataList.get(position).getStatus() != null){
            if(dataList.get(position).getStatus().equals("1")){
                holder.tvStatus.setText("OK");
            }else if(dataList.get(position).getStatus().equals("2")){
                holder.tvStatus.setText("TIDAK OK");
            } else {
                holder.tvStatus.setText("");
            }
        } else {
            holder.tvStatus.setText("");
        }

        if (!dataList.get(position).getExpandable()) {
            holder.llDetail.setVisibility(View.GONE);
            holder.ivBack.setRotation(270);
        } else {
            holder.llDetail.setVisibility(View.VISIBLE);
            holder.ivBack.setRotation(90);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDate, tvLocation, tvDesc, tvStatus;
        private LinearLayout llDetail;
        private ImageView ivBack, ivMaps;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            llDetail = itemView.findViewById(R.id.llDetail);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            ivBack = itemView.findViewById(R.id.ivBack);
            ivMaps = itemView.findViewById(R.id.ivMaps);
        }
    }
}
