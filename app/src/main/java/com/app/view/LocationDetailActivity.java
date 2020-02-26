package com.app.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.app.R;
import com.app.dao.tLocationDAO;
import com.app.fragment.ImagesFragment;
import com.app.fragment.ListMemoryFragment;
import com.app.fragment.LocationInfoFragment;
import com.app.model.tLocation;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import java.util.Random;

public class LocationDetailActivity extends AppCompatActivity {
    MaterialViewPager mViewPager;
    int location_id;
    tLocation location;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        location_id = getIntent().getIntExtra("locationId",1);
        location = new tLocationDAO(this).get(location_id);
        mViewPager = findViewById(R.id.materialViewPager);
        TextView tv = mViewPager.findViewById(R.id.header_logo);
        String logo = (location.getLocationName().length() > 40) ? location.getLocationName().substring(0,40)+"..." : location.getLocationName();
        tv.setText(logo);
        final Toolbar toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            @NonNull
            public Fragment getItem(int position) {
                switch (position % 3) {
                    case 0:
                        return LocationInfoFragment.newInstance(location_id);
                    case 1:
                        return ListMemoryFragment.newInstance(location_id);
                    default:
                        FragmentManager transaction = LocationDetailActivity.this.getSupportFragmentManager();
                        return ImagesFragment.newInstance(location_id, transaction);
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                        return getString(R.string.location_info);
                    case 1:
                        return getString(R.string.list_memories);
                    case 2:
                        return getString(R.string.location_images);
                }
                return "";
            }
        });
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                return HeaderDesign.fromColorResAndDrawable(
                        R.color.colorPrimaryDark,
                        randomDrawable());
            }
        });
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
    }

    private Drawable randomDrawable(){
        Drawable[] list = {getResources().getDrawable(R.drawable.background_1), getResources().getDrawable(R.drawable.background_10),
                getResources().getDrawable(R.drawable.background_2), getResources().getDrawable(R.drawable.background_11),
                getResources().getDrawable(R.drawable.background_3),  getResources().getDrawable(R.drawable.background_12),
                getResources().getDrawable(R.drawable.background_4),
                getResources().getDrawable(R.drawable.background_5),
                getResources().getDrawable(R.drawable.background_6),
                getResources().getDrawable(R.drawable.background_7),
                getResources().getDrawable(R.drawable.background_8),
                getResources().getDrawable(R.drawable.background_9)};

        return list[new Random().nextInt(list.length)];
    }
}
