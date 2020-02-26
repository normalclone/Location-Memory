package com.app.dao;

import java.util.List;

public interface DAO<T> {
    boolean save(T obj);
    boolean delete(int id);
    List<T> getAll();
    Object get(int id);
}
