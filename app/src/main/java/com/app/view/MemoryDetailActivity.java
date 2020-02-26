package com.app.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.adapter.MemoryImgAdapter;
import com.app.dao.ImgDAO;
import com.app.dao.MemoryDAO;
import com.app.dao.tLocationDAO;
import com.app.model.Memory;
import com.app.util.DateTimeUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Calendar;

public class MemoryDetailActivity extends AppCompatActivity{
    private TextView tvTitle;
    private TextView tvCreatedAt;
    private TextView tvMemoryContent;
    private RecyclerView recyclerView;
    private Button btnEdit;
    private Button btnDelete;
    private Memory memory;
    private int yy, MM, dd;
    private int year, month, day, hour, minute;
    private String createdAt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);
        memory = new MemoryDAO(this).get(getIntent().getIntExtra("id", 1));

        tvTitle = findViewById(R.id.tv_title_name);
        tvCreatedAt = findViewById(R.id.tv_created_at);
        tvMemoryContent = findViewById(R.id.memory_content);

        recyclerView = findViewById(R.id.image_card_zone);
        btnDelete = findViewById(R.id.delete_button);
        btnEdit = findViewById(R.id.edit_button);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MemoryDetailActivity.this);
                builder.setTitle(getString(R.string.memory_edit_title));

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewInflated = inflater.inflate(R.layout.edit_memory_dialog, null);
                final TextInputLayout tl_createdAt = viewInflated.findViewById(R.id.edtlo_created_at);
                final TextInputLayout tl_title = viewInflated.findViewById(R.id.edtlo_title);
                final TextInputLayout tl_content = viewInflated.findViewById(R.id.edtlo_content);
                final TextInputEditText edt_createdAt = viewInflated.findViewById(R.id.edt_created_at);
                final TextInputEditText edt_title = viewInflated.findViewById(R.id.edt_title);
                final TextInputEditText edt_content = viewInflated.findViewById(R.id.edt_content);

                TextInputEditText edt_locationName=  viewInflated.findViewById(R.id.edt_location_name);
                edt_locationName.setText(new tLocationDAO(MemoryDetailActivity.this).get(memory.getLocation_id()).getLocationName());
                edt_createdAt.setText(DateTimeUtil.convertDatetimeToString(memory.getCreated_at()));
                edt_title.setText(memory.getTitle());
                edt_content.setText(memory.getContent());
                tl_createdAt.setEndIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MemoryDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                yy = year;
                                MM = month+1;
                                dd = dayOfMonth;
                                TimePickerDialog timePickerDialog = new TimePickerDialog(MemoryDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String rs = yy+"/"+ DateTimeUtil.checkPickedNumber(MM)+"/"+DateTimeUtil.checkPickedNumber(dd)+" "+DateTimeUtil.checkPickedNumber(hourOfDay)+":"+DateTimeUtil.checkPickedNumber(minute)+":00";
                                        edt_createdAt.setText(rs);
                                    }
                                }, hour, minute, true);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                        datePickerDialog.show();
                    }
                });

                builder.setView(viewInflated);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean flag = true;
                        String title = edt_title.getText().toString();
                        String content = edt_content.getText().toString();
                        if(title.equals("")){
                            tl_title.setError(getString(R.string.err_memory_title_required)); flag = false;
                        }
                        if(flag){
                            memory.setContent(content);
                            memory.setTitle(title);
                            try {
                                memory.setCreated_at(DateTimeUtil.convertStringToDatetime(edt_createdAt.getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            new MemoryDAO(MemoryDetailActivity.this).save(memory);
                            setInfo();
                            dialog.dismiss();
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
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MemoryDetailActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.notice));
                alertDialogBuilder.setMessage(getString(R.string.are_you_sure)).setCancelable(false);
                alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete all the selected messages
                                if(new MemoryDAO(MemoryDetailActivity.this).delete(memory.getId())){
                                    Toast.makeText(MemoryDetailActivity.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                                    finish();
                                }else{
                                    Toast.makeText(MemoryDetailActivity.this, getString(R.string.failure), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                alertDialogBuilder.setNeutralButton(getString(R.string.no),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder.create().show();
            }
        });

        setInfo();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        setAdapter();
    }

    public void setAdapter(){
        MemoryImgAdapter adapter = new MemoryImgAdapter(this, new ImgDAO(this).getImgsByMemory(memory.getId()));
        recyclerView.setAdapter(adapter);
    }

    private void setInfo(){
        tvTitle.setText(memory.getTitle());
        tvCreatedAt.setText(DateTimeUtil.convertDatetimeToString(memory.getCreated_at()));
        tvMemoryContent.setText(memory.getContent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInfo();
        setAdapter();
    }
}
