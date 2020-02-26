package com.app.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.adapter.MemoryAdapter;
import com.app.dao.MemoryDAO;

import java.util.List;

public class ListMemoryActivity extends AppCompatActivity implements MemoryAdapter.OnClickListener{
    private RecyclerView recyclerView;
    private int viewPosition;
    private MemoryDAO dao;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private MemoryAdapter adapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_memory);
        dao = new MemoryDAO(this);
        actionModeCallback = new ActionModeCallback();

        setupTransitions();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter(){
        adapter = new MemoryAdapter(this, dao.getAll(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onLongClick(int position) {
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void setupTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", viewPosition);
    }

    @Override
    public void onItemClick(View sharedView, String transitionName, int position) {
        if (adapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        }else{
            viewPosition = position;
            Intent i = new Intent(ListMemoryActivity.this, MemoryDetailActivity.class);
            i.putExtra("id", (int) adapter.getItemId(position));
            startActivity(i);
        }
    }

    // deleting the messages from recycler view
    private void deleteSelectedItem() {
        adapter.resetAnimationIndex();
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            dao.delete((int)adapter.getItemId(selectedItemPositions.get(i)));
            adapter.removeData(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListMemoryActivity.this);
                    alertDialogBuilder.setTitle(getString(R.string.notice));
                    alertDialogBuilder.setMessage(getString(R.string.are_you_sure)).setCancelable(false);
                    alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete all the selected messages
                                    deleteSelectedItem();
                                    mode.finish();
                                }
                            });
                    alertDialogBuilder.setNeutralButton(getString(R.string.no),
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    dialog.cancel();
                                    mode.finish();
                                }
                            });
                    alertDialogBuilder.create().show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            toolbar.setVisibility(View.VISIBLE);
            adapter.getItemCount();
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.clearSelections();
                    adapter.resetAnimationIndex();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
