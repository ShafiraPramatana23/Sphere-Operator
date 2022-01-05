package com.example.sphere.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sphere.R;
import com.example.sphere.ui.home.model.River;

import java.util.ArrayList;

public class RiverSpinnerAdapter extends ArrayAdapter<River> {
    private ArrayList<River> dataList;

    public RiverSpinnerAdapter(Context context, int textViewResourceId, ArrayList<River> dataList) {
        super(context, textViewResourceId, dataList);
        this.dataList = dataList;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
        final TextView label = row.findViewById(R.id.tvText);

        River items = dataList.get(position);
        label.setText(items.getName());

        return row;
    }
}
