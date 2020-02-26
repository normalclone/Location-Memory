package com.app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.util.DB;
import com.app.model.Memory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MemoryDAO implements DAO<Memory> {
    private Memory savedMemory;
    private Context mContext;
    private String TABLE = "memory";
    public MemoryDAO(Context mContext){
        this.mContext = mContext;
    }
    @Override
    public boolean save(Memory obj) {
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("location_id", obj.getLocation_id());
        values.put("title", obj.getTitle());
        values.put("content", obj.getContent());
        values.put("created_at", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(obj.getCreated_at()));
        if(obj.getId() == 0){
            long id = db.insert(TABLE, null, values);
            savedMemory = this.get((int)id);
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
        new ImgDAO(mContext).deleteByMemory(id);
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        int rs = db.delete(TABLE,  "id = ?",
                new String[] { String.valueOf(id) });
        db.close();
        if(rs>0) return true;
        return false;
    }

    @Override
    public List<Memory> getAll() {
        List<Memory> list = new ArrayList<Memory>();
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE +" ORDER BY created_at DESC", null);
        try{
            if (c.moveToFirst()) {
                do {
                    Memory object = new Memory(
                            c.getInt(c.getColumnIndex("location_id")),
                            c.getInt(c.getColumnIndex("id")),
                            c.getString(c.getColumnIndex("title")),
                            c.getString(c.getColumnIndex("content")),
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(c.getColumnIndex("created_at")))
                    );
                    list.add(object);
                } while (c.moveToNext());
            }
            db.close();
            return list;
        }catch (Exception ex){
            ex.printStackTrace();
            db.close();
            return null;
        }
    }

    @Override
    public Memory get(int id) {
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE id = "+id, null);
        if (c != null) c.moveToFirst();
        try{
            db.close();
            return new Memory(
                    c.getInt(c.getColumnIndex("location_id")),
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("title")),
                    c.getString(c.getColumnIndex("content")),
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(c.getColumnIndex("created_at")))
            );
        }catch (Exception ex){
            db.close();
            ex.printStackTrace();
            return null;
        }
    }

    public Memory getLastSavedMemory() {
        return savedMemory;
    }

    public List<Memory> getMemoriesByLocation(int location_id) {
        List<Memory> list = new ArrayList<Memory>();
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE location_id = " + location_id + " ORDER BY created_at DESC", null);
        try {
            if (c.moveToFirst()) {
                do {
                    Memory object = new Memory(
                            c.getInt(c.getColumnIndex("location_id")),
                            c.getInt(c.getColumnIndex("id")),
                            c.getString(c.getColumnIndex("title")),
                            c.getString(c.getColumnIndex("content")),
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(c.getColumnIndex("created_at")))
                    );
                    list.add(object);
                } while (c.moveToNext());
            }
            db.close();
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            db.close();
            return null;
        }
    }

    public void deleteByLocation(int location_id){
        for(Memory i : getMemoriesByLocation(location_id)){
            delete(i.getId());
        }
    }
}
