package com.app.model;

import android.content.Context;

import com.app.dao.ImgDAO;
import com.app.dao.tLocationDAO;

import java.util.Date;
import java.util.List;

public class Memory {
    private int location_id;
    private int id;
    private String title;
    private String content;
    private Date created_at;

    public Memory(int location_id, int id, String title, String content, Date created_at) {
        this.location_id = location_id;
        this.id = id;
        this.title = title;
        this.content = content;
        this.created_at = created_at;
    }

    public Memory() {}

    public List<Img> getImgs(Context mContext){
        return new ImgDAO(mContext).getImgsByMemory(this.id);
    }

    public void setImgs(List<Img> list, Context mContext){
        ImgDAO dao = new ImgDAO(mContext);
        for(Img i: list){
            i.setMemory_id(this.id);
            dao.save(i);
        }
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public tLocation getLocation(Context mContext){
        tLocation location = new tLocationDAO(mContext).get(this.id);
        return location;
    }

    public void setLocation(tLocation location){
        this.location_id = location.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Memory{" +
                "location_id=" + location_id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
