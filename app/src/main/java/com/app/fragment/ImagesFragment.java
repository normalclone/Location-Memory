package com.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.adapter.ImgAdapter;
import com.app.dao.ImgDAO;
import com.app.dao.MemoryDAO;
import com.app.model.Img;
import com.app.model.Memory;
import com.app.view.LocationDetailActivity;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import java.util.ArrayList;
import java.util.List;

public class ImagesFragment extends Fragment {
    RecyclerView mRecyclerView;
    int viewPosition;
    private int location_id;
    FragmentManager fragmentManager;

    public static ImagesFragment newInstance(int location_id,FragmentManager fragmentManager) {
        return new ImagesFragment(location_id, fragmentManager);
    }

    public ImagesFragment(int location_id, FragmentManager fragmentManager){
        this.location_id = location_id;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycle_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //setup materialviewpager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        setAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    public void setAdapter(){
        List<Img> list = new ArrayList<Img>();
        for(Memory i: new MemoryDAO(getContext()).getMemoriesByLocation(location_id)){
            list.addAll(new ImgDAO(getContext()).getImgsByMemory(i.getId()));
        }
        ImgAdapter adapter = new ImgAdapter(getContext(), list, fragmentManager, this);
        //Use this now
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", viewPosition);
    }
}
