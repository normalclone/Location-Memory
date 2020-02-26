package com.app.model;

import android.content.Context;

import com.app.dao.MemoryDAO;

public class Img {
    private int id;
    private int memory_id;
    private int order;
    private String link;

    @Override
    public String toString() {
        return "Img{" +
                "id=" + id +
                ", memory_id=" + memory_id +
                ", order=" + order +
                ", link='" + link + '\'' +
                '}';
    }

    public Memory getMemory(Context mContext){
        return new MemoryDAO(mContext).get(memory_id);
    }

    public void setMemory(Memory memory){
        memory_id = memory.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemory_id() {
        return memory_id;
    }

    public void setMemory_id(int memory_id) {
        this.memory_id = memory_id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Img(int id, int memory_id, int order, String link) {
        this.id = id;
        this.memory_id = memory_id;
        this.order = order;
        this.link = link;
    }

    public Img() {
    }
}
