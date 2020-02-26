package com.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.app.R;
import com.app.util.MapboxUtil;
import com.app.view.MainActivity;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.List;

public class WayStopAdapter extends ArrayAdapter<Location> {
    private MainActivity activity;
    private int resource;
    private List<Location> list;

    public WayStopAdapter(MainActivity activity, int resource , List<Location> list) {
        super(activity, resource, list);
        this.activity = activity;
        this.resource = resource;
        this.list= list;
    }
    public class ViewHolder{
        private TextView location_name;
        private ImageButton remove;
        private CardView container;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(activity).inflate(resource ,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.remove=(ImageButton)convertView.findViewById(R.id.btn_remove);
            viewHolder.location_name=(TextView)convertView.findViewById(R.id.tv_location_name);
            viewHolder.container = convertView.findViewById(R.id.container);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        Location location= list.get(position);

        viewHolder.location_name.setText(activity.getString(R.string.latlng)+(position+1)+"\n ("+location.getLatitude()+","+location.getLongitude()+")");
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                activity.refreshRoute();
                notifyDataSetChanged();
                if(list.size() == 1) activity.DisableFabStartNavigation();
                if(list.size() == 0) activity.dissmistDialogAndDisableFabDirectorList();
            }
        });
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location temp = list.get(position);
                activity.dismissDialogAndFlyToLocation(temp);
            }
        });

        return convertView;
    }
}
