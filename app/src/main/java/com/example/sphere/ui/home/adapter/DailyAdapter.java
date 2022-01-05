package com.example.sphere.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sphere.R;
import com.example.sphere.ui.home.model.Daily;
import com.example.sphere.ui.home.model.Hourly;
import com.example.sphere.util.DateFormatter;

import java.text.ParseException;
import java.util.List;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    private List<Daily> list;
    private LayoutInflater mInflater;
    private Context mContext;

    public DailyAdapter(Context context, List<Daily> number) {
        this.mInflater = LayoutInflater.from(context);
        this.list = number;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_daily, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Daily item = list.get(position);
        DateFormatter df = new DateFormatter();
        holder.tvHour.setText(df.UTCtoDate(item.getHour()));
        holder.tvTemp.setText(item.getTemp());

        int id = Integer.parseInt(item.getIdIcon());
        if (id >= 200 && id < 300) {
            holder.iv.setBackgroundResource(R.drawable.ic_thunder);
        } else if (id >= 500 && id < 600) {
            holder.iv.setBackgroundResource(R.drawable.ic_rain);
        } else if (id >= 800) {
            holder.iv.setBackgroundResource(R.drawable.ic_cloud_2);
        } else {
            holder.iv.setBackgroundResource(R.drawable.ic_cloud_2);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvHour, tvTemp;
        ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHour = itemView.findViewById(R.id.tvHour);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}
