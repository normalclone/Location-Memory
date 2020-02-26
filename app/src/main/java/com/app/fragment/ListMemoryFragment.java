package com.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.adapter.MemoryAdapter;
import com.app.adapter.MemoryOnlyAdapter;
import com.app.dao.MemoryDAO;
import com.app.view.MemoryDetailActivity;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

public class ListMemoryFragment extends Fragment implements MemoryAdapter.OnClickListener {
    RecyclerView mRecyclerView;
    MemoryOnlyAdapter adapter;
    int viewPosition;
    private int location_id;

    public static ListMemoryFragment newInstance(int location_id) {
        return new ListMemoryFragment(location_id);
    }

    public ListMemoryFragment(int location_id){
        this.location_id = location_id;
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
        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        setAdapter();
    }

    @Override
    public void onItemClick(View sharedView, String transitionName, int position) {
        viewPosition = position;
        Intent i = new Intent(getContext(), MemoryDetailActivity.class);
        i.putExtra("id", (int) adapter.getItemId(position));
        startActivity(i);
    }

    @Override
    public void onLongClick(int position) { }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", viewPosition);
    }

    public void setAdapter(){
        adapter = new MemoryOnlyAdapter(getContext(), new MemoryDAO(getContext()).getMemoriesByLocation(location_id));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }
}
