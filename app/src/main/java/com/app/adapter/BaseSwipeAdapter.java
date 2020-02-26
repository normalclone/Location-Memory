package com.app.adapter;

import android.widget.BaseAdapter;

abstract class BaseSwipeAdapter extends BaseAdapter {

    public boolean getSwipEnableByPosition(int position){
        return true;
    }

}