package com.app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.model.Place;
import com.app.util.DB;
import java.util.ArrayList;
import java.util.List;

public class PlaceDAO implements DAO<Place> {
    private Context mContext;
    private String TABLE = "Place";
    public PlaceDAO(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public boolean save(Place obj) {
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("place_id", obj.getPlace_id());
        values.put("name", obj.getName());
        values.put("description", obj.getAddress());
        long id = db.insert(TABLE, null, values);
        db.close();
        if(id!=0) return true;
        return false;
    }

    @Override
    public boolean delete(int id) {
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        int rs = db.delete(TABLE,  "id = ?",
                new String[] { String.valueOf(id) });
        db.close();
        if(rs>0) return true;
        return false;
    }

    public boolean deleteAll(){
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE);
        db.close();
        return true;
    }

    @Override
    public List<Place> getAll() {
        List<Place> list = new ArrayList<>();
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE, null);
        try{
            if (c.moveToFirst()) {
                do {
                    Place temp = new Place();
                    temp.setName(c.getString(c.getColumnIndex("name")));
                    temp.setAddress(c.getString(c.getColumnIndex("description")));
                    temp.setPlace_id(c.getString(c.getColumnIndex("place_id")));
                    list.add(temp);
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
    public Place get(int id) {
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE id = "+id, null);
        if (c != null) c.moveToFirst();
        try{
            Place temp = new Place();
            temp.setName(c.getString(c.getColumnIndex("name")));
            temp.setAddress(c.getString(c.getColumnIndex("description")));
            temp.setPlace_id(c.getString(c.getColumnIndex("place_id")));
            db.close();
            return temp;
        }catch (Exception ex){
            ex.printStackTrace();
            db.close();
            return null;
        }
    }
}
