package com.example.sphere;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sphere.R;
import com.example.sphere.ui.auth.RegisterActivity;
import com.example.sphere.ui.profile.model.MyReportList;
import com.example.sphere.util.DateFormatter;

import java.util.ArrayList;

public class ListComplainAdapter extends RecyclerView.Adapter<ListComplainAdapter.UserViewHolder> {
    private ArrayList<Complain> dataList;
    private Context context;

    public ListComplainAdapter(ArrayList<Complain> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_complain, parent, false);
        context = parent.getContext();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Glide.with(context)
                .load(dataList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.ivMaps);

        holder.tvTitle.setText(dataList.get(position).getTitle());
        holder.tvDate.setText(new DateFormatter().convertDate(dataList.get(position).getDate()));
        holder.tvLocation.setText(dataList.get(position).getAddress());
        holder.tvCtg.setText(dataList.get(position).getCategory());
        holder.tvDesc.setText(dataList.get(position).getDesc());
        holder.tvStatus.setText(dataList.get(position).getProgress());

        holder.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.get(position).setExpandable(!dataList.get(position).getExpandable());
                notifyItemChanged(position);
            }
        });

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(context, DetailComplainActivity.class);
                m.putExtra("id", dataList.get(position).getId());
                context.startActivity(m);
//                finish();
            }
        });


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
        private TextView tvTitle, tvDate, tvLocation, tvCtg, tvDesc , tvStatus;
        private LinearLayout llDetail;
        private ImageView ivBack, ivMaps;
        private RelativeLayout btnView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            llDetail = itemView.findViewById(R.id.llDetail);
            tvCtg = itemView.findViewById(R.id.tvCtg);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            ivBack = itemView.findViewById(R.id.ivBack);
            ivMaps = itemView.findViewById(R.id.ivMaps);
            tvStatus = itemView.findViewById(R.id.tvStatuss);
            btnView = itemView.findViewById(R.id.btnDetail);
        }
    }
}
