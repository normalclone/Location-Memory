package com.app.view;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.adapter.MemoryImgAdapter;
import com.app.dao.ImgDAO;
import com.app.dao.MemoryDAO;
import com.app.dao.tLocationDAO;
import com.app.model.Img;
import com.app.model.Memory;
import com.app.util.DateTimeUtil;
import com.app.util.SystemUtil;
import com.app.util.WindowUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MemoryDetailActivity extends AppCompatActivity{
    private TextView tvTitle;
    private TextView tvCreatedAt;
    private TextView tvMemoryContent;
    private RecyclerView recyclerView;
    private Memory memory;
    private int yy, MM, dd;
    private int year, month, day, hour, minute;
    private int REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private String uri_cameraPicture;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);
        memory = new MemoryDAO(this).get(getIntent().getIntExtra("id", 1));


        //Status bar
        final RelativeLayout container = findViewById(R.id.expandView);
        LinearLayout.LayoutParams containerParam = (LinearLayout.LayoutParams) container.getLayoutParams();
        containerParam.topMargin = WindowUtil.getStatusBarHeight(this);
        container.setLayoutParams(containerParam);

        tvTitle = findViewById(R.id.tv_title_name);
        tvCreatedAt = findViewById(R.id.tv_created_at);
        tvMemoryContent = findViewById(R.id.memory_content);

        recyclerView = findViewById(R.id.image_card_zone);
        Button btnDelete = findViewById(R.id.delete_button);
        Button btnEdit = findViewById(R.id.edit_button);
        Button addImage = findViewById(R.id.btn_add_image);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MemoryDetailActivity.this);
                    builder.setTitle(getString(R.string.dialog_add_picture_title));

                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (items[i].equals(getString(R.string.camera))) {

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                if(intent.resolveActivity(getPackageManager()) != null){
                                    File f = null;
                                    try {
                                        f = getImageFile();
                                        uri_cameraPicture = f.getAbsolutePath();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if(f != null){
                                        Uri uri = FileProvider.getUriForFile(MemoryDetailActivity.this, "com.app.fileprovider", f);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                    }
                                }

                                startActivityForResult(intent, REQUEST_CAMERA);

                            } else if (items[i].equals(getString(R.string.gallery))) {

                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                startActivityForResult(intent, SELECT_FILE);

                            } else if (items[i].equals(getString(R.string.cancel))) {
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MemoryDetailActivity.this);
                builder.setTitle(getString(R.string.memory_edit_title));

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewInflated = inflater.inflate(R.layout.edit_memory_dialog, null);
                final TextInputLayout tl_createdAt = viewInflated.findViewById(R.id.edtlo_created_at);
                final TextInputLayout tl_title = viewInflated.findViewById(R.id.edtlo_title);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Img temp = new Img();
                temp.setLink(SystemUtil.getPath(MemoryDetailActivity.this, Uri.parse("file://" + uri_cameraPicture)));
                temp.setMemory_id(memory.getId());
                temp.setOrder(1000);
                new ImgDAO(MemoryDetailActivity.this).save(temp);
            } else if (requestCode == SELECT_FILE) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        Img temp = new Img();
                        temp.setLink(SystemUtil.getPath(MemoryDetailActivity.this, uri));
                        temp.setMemory_id(memory.getId());
                        temp.setOrder(1);
                        new ImgDAO(MemoryDetailActivity.this).save(temp);
                    }
                } else {
                    Uri selectedImageUri = data.getData();
                    Img temp = new Img();
                    temp.setLink(SystemUtil.getPath(MemoryDetailActivity.this, selectedImageUri));
                    temp.setMemory_id(memory.getId());
                    temp.setOrder(1);
                    new ImgDAO(MemoryDetailActivity.this).save(temp);
                }
            }
        }
        onResume();
    }


    private File getImageFile() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "ipg_"+time+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageName, ".jpg", storageDir);
        return f;
    }

    public  boolean isStoragePermissionGranted() {
        boolean f = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                f = true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                f = false;
            }

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                f = true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                f =  false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            f = true;
        }
        return f;
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
