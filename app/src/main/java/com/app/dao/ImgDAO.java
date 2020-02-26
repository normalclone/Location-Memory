package com.app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.util.DB;
import com.app.model.Img;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class  ImgDAO implements DAO<Img>{
    private Context mContext;
    private String TABLE = "images";
    public ImgDAO(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public boolean save(Img obj) {
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("memory_id", obj.getMemory_id());
        values.put("img_order", obj.getOrder());
        values.put("link", obj.getLink());
        if(obj.getId()==0){
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
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        int rs = db.delete(TABLE,  "id = ?",
                new String[] { String.valueOf(id) });
        db.close();
        if(rs>0) return true;
        return false;
    }

    @Override
    public List<Img> getAll() {
        List<Img> list = new ArrayList<Img>();
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE +" ORDER BY created_at DESC", null);
        try{
            if (c.moveToFirst()) {
                do {
                    Img object = new Img(
                            c.getInt(c.getColumnIndex("id")),
                            c.getInt(c.getColumnIndex("memory_id")),
                            c.getInt(c.getColumnIndex("img_order")),
                            c.getString(c.getColumnIndex("link"))
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
    public Img get(int id) {
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE id = "+id, null);
        if (c != null) c.moveToFirst();
        try{
            db.close();
            return new Img(
                    c.getInt(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("memory_id")),
                    c.getInt(c.getColumnIndex("img_order")),
                    c.getString(c.getColumnIndex("link"))
            );
        }catch (Exception ex){
            ex.printStackTrace();
            db.close();
            return null;
        }
    }

    public Img getByLink(String link) {
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE link = '"+link+"'", null);
        if (c != null) c.moveToFirst();
        try{
            db.close();
            return new Img(
                    c.getInt(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("memory_id")),
                    c.getInt(c.getColumnIndex("img_order")),
                    c.getString(c.getColumnIndex("link"))
            );
        }catch (Exception ex){
            ex.printStackTrace();
            db.close();
            return null;
        }
    }

    public List<Img> getImgsByMemory(int memory_id) {
        List<Img> list = new ArrayList<Img>();
        SQLiteDatabase db = new DB(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE + " WHERE memory_id = "+memory_id, null);
        try{
            if (c.moveToFirst()) {
                do {
                    Img object = new Img(
                            c.getInt(c.getColumnIndex("id")),
                            c.getInt(c.getColumnIndex("memory_id")),
                            c.getInt(c.getColumnIndex("img_order")),
                            c.getString(c.getColumnIndex("link"))
                    );
                    list.add(object);
                } while (c.moveToNext());
            }
            Comparator<Img> compare = new Comparator<Img>() {
                @Override
                public int compare(Img o1, Img o2) {
                    return o1.getOrder() - (o2.getOrder());
                }
            };
            Collections.sort(list, compare);
            db.close();
            return list;
        }catch (Exception ex){
            db.close();
            ex.printStackTrace();
            return null;
        }
    }

    public void deleteByMemory(int memory_id){
        SQLiteDatabase db = new DB(mContext).getWritableDatabase();
        int rs = db.delete(TABLE,  "memory_id = ?",
                new String[] { String.valueOf(memory_id) });
        db.close();
    }
}
