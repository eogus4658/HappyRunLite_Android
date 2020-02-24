package com.example.happyrunlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<RecordFragment.RecordData> arritemdata;

    public ListViewAdapter(Context context, ArrayList<RecordFragment.RecordData> data) {
        mContext = context;
        arritemdata = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return arritemdata.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecordFragment.RecordData getItem(int position) {
        return arritemdata.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_item, null);

        TextView record = (TextView)view.findViewById(R.id.record);
        TextView date = (TextView)view.findViewById(R.id.date);

        record.setText(arritemdata.get(position).getRecord());
        date.setText(arritemdata.get(position).getDate());

        return view;
    }


}