package com.mobile.finalproject.ma01_20190981;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TrackingInfoAdapter extends BaseAdapter {

    public static final String TAG = "TrackingInfoAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<TrackingInfo> list;
    private TrackingInfoNetworkManager networkManager = null;


    public TrackingInfoAdapter(Context context, int resource, ArrayList<TrackingInfo> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        networkManager = new TrackingInfoNetworkManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public TrackingInfo getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return list.get(position).getRnum();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tvTitle);
            viewHolder.tvSpatial = view.findViewById(R.id.tvSpatial);
            viewHolder.tvAlternativeTitle = view.findViewById(R.id.tvAlternativeTitle);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        TrackingInfo dto = list.get(position);

        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvSpatial.setText(dto.getSpatial());
        viewHolder.tvAlternativeTitle.setText(dto.getAlternativeTitle());

        return view;
    }


    public void setList(ArrayList<TrackingInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

//    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvSpatial = null;
        public TextView tvAlternativeTitle = null;
    }
}
