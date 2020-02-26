package com.app.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.R;
import com.app.adapter.tLocationAdapter;
import com.app.dao.tLocationDAO;
import com.app.model.tLocation;
import com.app.util.SystemUtil;
import com.app.util.WindowUtil;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.List;

public class ListLocationActivity extends AppCompatActivity {
    private tLocationDAO dao;
    private List<tLocation> list;
    private SwipeMenuListView listView;
    private Location currentLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_location);
        dao = new tLocationDAO(ListLocationActivity.this);
        currentLocation = new Location("Current location");
        currentLocation.setLatitude(this.getIntent().getDoubleExtra("latitude", 1));
        currentLocation.setLongitude(this.getIntent().getDoubleExtra("longitude", 1));
        list = dao.getAll();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = findViewById(R.id.lv_list_location);
        refreshTheList();
    }

    private void refreshTheList(){
        list = dao.getAll();
        setAdapter();
    }

    @Override
    protected void onResume() {
        SystemUtil.hideLoading();
        super.onResume();
        refreshTheList();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setAdapter(){
        tLocationAdapter adapter = new tLocationAdapter(ListLocationActivity.this, R.layout.listitem_location_adapter, list, currentLocation);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SystemUtil.showLoading(getString(R.string.loading), ListLocationActivity.this);
                Intent i = new Intent(ListLocationActivity.this, LocationDetailActivity.class);
                i.putExtra("locationId", list.get(position).getId());
                startActivity(i);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(R.drawable.empty_rectangle_v2);
                deleteItem.setWidth(WindowUtil.dpToPixels(ListLocationActivity.this, 50));
                deleteItem.setIcon(R.drawable.ic_delete_black_24dp);
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final tLocation item = list.get(position);
                switch (index) {
                    case 0:{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListLocationActivity.this);
                        builder.setTitle(getString(R.string.menu_item_title));

                        LayoutInflater  inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View viewInflated = inflater.inflate(R.layout.edit_location_dialog, null);
                        final EditText input = viewInflated.findViewById(R.id.input);
                        builder.setView(viewInflated);

                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                String m_Text = input.getText().toString();
                                if(!m_Text.equals("")){
                                    item.setLocationName(m_Text);
                                    dao.save(item);

                                    refreshTheList();
                                }else{
                                    dialog.cancel();
                                }
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }break;
                    case 1:{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListLocationActivity.this);
                        builder.setTitle(getString(R.string.notice));
                        builder.setMessage(getString(R.string.menu_item_delete_confirm));
                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dao.delete(item.getId());
                                refreshTheList();
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }break;
                }
                return false;
            }
        });
    }
}
