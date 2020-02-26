package com.app.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.model.tLocation;
import com.app.util.MapboxUtil;
import com.app.view.MainActivity;

import java.util.List;

public class SearchLocalResultAdapter extends RecyclerView.Adapter<SearchLocalResultAdapter.ViewHolder> {
    private List<tLocation> list;
    private Context context;
    private Location currentLocation;
    private String kw;
    private MainActivity activity;

    public SearchLocalResultAdapter(Context context, List<tLocation> objects, Location currentLocation, String kw, MainActivity act) {
        this.context = context;
        this.list= objects;
        this.currentLocation = currentLocation;
        this.kw = kw;
        this.activity = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_search_local, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView locationName;
        TextView tvDistance;

        public ViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.tv_location_name);
            tvDistance = itemView.findViewById(R.id.tv_distance);
        }

        public void bind(final ViewHolder holder, final int position) {
            final tLocation temp = list.get(position);
            locationName.setText(temp.getLocationName());
            int distance = (int) MapboxUtil.calculateBetween2Location(currentLocation, temp.getLocation());
            String distanceStr = (distance > 1000) ? (int)(distance/1000)+" km " : distance+ " m ";
            tvDistance.setText(distanceStr);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.flyToSavedLocation(temp);
                }
            });
        }
    }
}
