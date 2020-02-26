package com.app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.app.util.DB;
import com.app.model.tLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class tLocationDAO implements DAO<tLocation> {
    private Context mContext;
    private String TABLE = "tLocation";
    public tLocationDAO(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public boolean save(tLocation obj) {
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("location_name", obj.getLocationName());
        values.put("latitude", obj.getLocation().getLatitude());
        values.put("longitude", obj.getLocation().getLongitude());
        values.put("created_at", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(obj.getCreated_at()));
        if(obj.getId() == 0){
            long id = db.insert(TABLE, null, values);
            db.close();
            if(id != 0) return true;
        }else{
            int rs = db.update(TABLE, values, "id = ?", new String[]{String.valueOf(obj.getId())});
            db.close();
            if(rs > 0) return true;
        }
        db.close();
        return false;
    }

    @Override
    public boolean delete(int id) {
        new MemoryDAO(mContext).deleteByLocation(id);
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        int rs = db.delete(TABLE,  "id = ?",
                new String[] { String.valueOf(id) });
        db.close();
        if(rs>0) return true;
        return false;
    }

    @Override
    public List<tLocation> getAll() {
        List<tLocation> list = new ArrayList<>();
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE +" ORDER BY created_at DESC", null);
        try{
            if (c.moveToFirst()) {
                do {
                    Location location = new Location("Location");
                    location.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
                    location.setLongitude(c.getDouble(c.getColumnIndex("longitude")));

                    tLocation object = new tLocation(
                            c.getInt(c.getColumnIndex("id")),
                            c.getString(c.getColumnIndex("location_name")),
                            location,
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(c.getColumnIndex("created_at")))
                    );
                    list.add(object);
                } while (c.moveToNext());
            }
            db.close();
            return list;
        }catch (Exception ex){
            db.close();
            ex.printStackTrace();
            return null;
        }
    }

    public List<tLocation> search(String str) {
        List<tLocation> list = new ArrayList<>();
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE location_name LIKE '%"+str+"%'"+" ORDER BY created_at DESC", null);
        try{
            if (c.moveToFirst()) {
                do {
                    Location location = new Location("Location");
                    location.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
                    location.setLongitude(c.getDouble(c.getColumnIndex("longitude")));

                    tLocation object = new tLocation(
                            c.getInt(c.getColumnIndex("id")),
                            c.getString(c.getColumnIndex("location_name")),
                            location,
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(c.getColumnIndex("created_at")))
                    );
                    list.add(object);
                } while (c.moveToNext());
            }
            db.close();
            return list;
        }catch (Exception ex){
            db.close();
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public tLocation get(int id) {
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE id = "+id, null);
        if (c != null) c.moveToFirst();
        try{
            Location location = new Location("Location");
            location.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
            location.setLongitude(c.getDouble(c.getColumnIndex("longitude")));
            db.close();
            return new tLocation(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("location_name")),
                    location,
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(c.getColumnIndex("created_at")))
            );
        }catch (Exception ex){
            ex.printStackTrace();
            db.close();
            return null;
        }
    }

    public tLocation getByLocation(Location location) {
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE latitude = "+location.getLatitude()+" AND longitude = "+ location.getLongitude()+" ORDER BY created_at DESC", null);
        if (c != null) c.moveToFirst();
        try{
            Location loc = new Location("Return location");
            loc.setLatitude(location.getLatitude());
            loc.setLongitude(location.getLongitude());
            db.close();
            return new tLocation(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("location_name")),
                    loc,
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(c.getColumnIndex("created_at")))
            );
        }catch (Exception ex){
            db.close();
            return null;
        }
    }
}
