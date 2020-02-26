package com.app.model;

import android.content.Context;
import android.location.Location;

import com.app.dao.MemoryDAO;

import java.util.Date;
import java.util.List;

public class tLocation {
    private int id;
    private String locationName;
    private Location location;
    private Date created_at;

    public List<Memory> getMemories(Context mContext){
        return new MemoryDAO(mContext).getMemoriesByLocation(id);
    }

    public void setMemories(List<Memory> list,Context mContext){
        MemoryDAO dao = new MemoryDAO(mContext);
        for(Memory i : list){
            i.setLocation_id(this.id);
            dao.save(i);
        }
    }

    public tLocation() {}

    public tLocation(int id, String locationName, Location location, Date created_at) {
        this.id = id;
        this.locationName = locationName;
        this.location = location;
        this.created_at = created_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "tLocation{" +
                "id=" + id +
                ", locationName='" + locationName + '\'' +
                ", location=" + location +
                ", created_at=" + created_at +
                '}';
    }
}
