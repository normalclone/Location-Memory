package com.app.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.R;
import com.app.model.tLocation;
import com.app.util.DateTimeUtil;
import com.app.util.MapboxUtil;

import java.util.List;

public class tLocation2OverrideAdapter extends ArrayAdapter<tLocation> {
    private Context context;
    private int resource;
    private List<tLocation> list;
    private Location currentLocation;

    public tLocation2OverrideAdapter(Context context, int resource, List<tLocation> objects, Location currentLocation) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list= objects;
        this.currentLocation = currentLocation;
    }
    public class ViewHolder{
        private TextView discovery_date;
        private TextView location_name;
        private TextView distance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(resource ,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.discovery_date=(TextView)convertView.findViewById(R.id.tv_discovery_date);
            viewHolder.location_name=(TextView)convertView.findViewById(R.id.tv_location_name);
            viewHolder.distance=(TextView)convertView.findViewById(R.id.tv_distance);

            LinearLayout.LayoutParams prDistance = (LinearLayout.LayoutParams) viewHolder.distance.getLayoutParams();

            RelativeLayout.LayoutParams prName = (RelativeLayout.LayoutParams)viewHolder.location_name.getLayoutParams();
            prName.setMargins(0, 0, prDistance.width + prDistance.rightMargin + 5, 0);
            viewHolder.location_name.setLayoutParams(prName);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        tLocation location= list.get(position);

        viewHolder.discovery_date.setText(context.getString(R.string.DD)+" "+ DateTimeUtil.convertDateToString(location.getCreated_at()));
        viewHolder.location_name.setText(""+location.getLocationName());
        viewHolder.distance.setText(""+ (int)MapboxUtil.calculateBetween2Location(currentLocation, location.getLocation())+"m ");
        return convertView;
    }
}
