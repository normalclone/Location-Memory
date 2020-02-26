package com.app.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.model.Place;
import com.app.util.PlaceUtil;
import com.app.view.MainActivity;

import java.util.List;

public class SearchGoogleResultAdapter extends RecyclerView.Adapter<SearchGoogleResultAdapter.ViewHolder> {
    private List<Place> list;
    private Context context;
    private MainActivity activity;

    public SearchGoogleResultAdapter(Context context, List<Place> objects, MainActivity mainActivity) {
        this.context = context;
        this.list= objects;
        this.activity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_search_google, parent, false));
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
        TextView tvAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.tv_location_name);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }

        public void bind(final ViewHolder holder, final int position) {
            final Place temp = list.get(position);
            locationName.setText(temp.getName());
            String distanceStr = "";
            if(temp.getDistance() != 0) {
                distanceStr = (temp.getDistance() > 1000) ? (int)(temp.getDistance()/1000)+" km " : temp.getDistance()+ " m ";
            }
            tvDistance.setText(distanceStr);

            String address = (temp.getAddress().length() > 50) ? temp.getAddress().substring(0, 50)+"..." : temp.getAddress();
            tvAddress.setText(address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlaceUtil.getPlaceDetail(context, temp, activity);
                }
            });
        }
    }
}
