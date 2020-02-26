package com.app.view;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.app.R;
import com.app.dao.ImgDAO;
import com.app.dao.MemoryDAO;
import com.app.dao.tLocationDAO;
import com.app.model.Img;
import com.app.model.Memory;
import com.app.model.tLocation;
import com.app.util.DateTimeUtil;
import com.app.util.SystemUtil;
import com.app.util.WindowUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SaveMemoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private int yy, MM, dd;
    private int year, month, day, hour, minute;
    private int REQUEST_CAMERA = 1, SELECT_FILE = 0;

    private LinearLayout imageZone;
    private TextInputLayout edtlo_createdAt;
    private TextInputLayout edtlo_title;
    private TextInputLayout edtlo_content;

    private TextInputEditText edt_locationName;
    private TextInputEditText edt_createdAt;
    private TextInputEditText edt_title;
    private TextInputEditText edt_content;
    private Toolbar toolbar;

    private List<String> uris_cameraPicture = new ArrayList<>();
    private List<String> filePaths_cameraPicture = new ArrayList<>();
    private String uri_cameraPicture = "";
    private List<Uri> listImageUri;

    private ImageButton btn_addImage;
    private MaterialButton btn_saveMemory;
    private tLocation location;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);
        listImageUri = new ArrayList<>();

        edtlo_createdAt = findViewById(R.id.edtlo_created_at);
        edtlo_title = findViewById(R.id.edtlo_title);
        edtlo_content = findViewById(R.id.edtlo_content);

        edt_locationName = findViewById(R.id.edt_location_name);
        edt_createdAt = findViewById(R.id.edt_created_at);
        edt_title = findViewById(R.id.edt_title);
        edt_content = findViewById(R.id.edt_content);
        btn_addImage = findViewById(R.id.btn_add_image);
        btn_saveMemory = findViewById(R.id.btn_save_memory);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {for(int i = 0; i < uris_cameraPicture.size(); i++){SystemUtil.deleteFile(filePaths_cameraPicture.get(i)); } finish();}});
        imageZone = findViewById(R.id.image_zone);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        edt_createdAt.setText(year+"/"+DateTimeUtil.checkPickedNumber(month+1)+"/"+DateTimeUtil.checkPickedNumber(day)+" "+DateTimeUtil.checkPickedNumber(hour)+":"+DateTimeUtil.checkPickedNumber(minute)+":00");
        edtlo_createdAt.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SaveMemoryActivity.this, SaveMemoryActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        location = new tLocationDAO(this).get(this.getIntent().getIntExtra("locationId", 1));
        edt_locationName.setText(location.getLocationName());

        btn_addImage.setOnClickListener(btnAddImageClicked);
        btn_saveMemory.setOnClickListener(btnSaveMemoryClicked);
        isStoragePermissionGranted();
    }

    private View.OnClickListener btnSaveMemoryClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String title = edt_title.getText()+"";
            String content = edt_content.getText()+"";
            boolean flag = true;
            if(title.equals("")){
                edtlo_title.setError(getString(R.string.err_memory_title_required)); flag = false;
            }
            if(flag){
                try {
                    MemoryDAO memoryDAO = new MemoryDAO(SaveMemoryActivity.this);
                    Memory memory = new Memory(location.getId(), 0, title, content, DateTimeUtil.convertStringToDatetime(edt_createdAt.getText()+""));
                    if(memoryDAO.save(memory)){
                        if(listImageUri.size() != 0){
                            ImgDAO imgDao = new ImgDAO(SaveMemoryActivity.this);
                            int c = 0;
                            for(Uri i: listImageUri){
                                imgDao.save(new Img(0,
                                        memoryDAO.getLastSavedMemory().getId(),
                                        c,
                                        SystemUtil.getPath(SaveMemoryActivity.this, i)
                                ));
                            }
                        }
                        Toast.makeText(SaveMemoryActivity.this, getString(R.string.rs_success_save_memory)+title, Toast.LENGTH_LONG).show();
                        animate2CloseIntent();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void animate2CloseIntent(){
        final RelativeLayout root = findViewById(R.id.root);
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                root.setVisibility(View.GONE);
                SaveMemoryActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        root.startAnimation(anim);
    }
    private View.OnClickListener btnAddImageClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(listImageUri == null) listImageUri = new ArrayList<Uri>();
            final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel)};
            AlertDialog.Builder builder = new AlertDialog.Builder(SaveMemoryActivity.this);
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
                                uris_cameraPicture.add(f.getAbsolutePath());
                                uri_cameraPicture = f.getAbsolutePath();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(f != null){
                                Uri uri = FileProvider.getUriForFile(SaveMemoryActivity.this, "com.app.fileprovider", f);
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
    };

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {super.onRequestPermissionsResult(requestCode, permissions, grantResults);}

    private File getImageFile() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "ipg_"+time+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageName, ".jpg", storageDir);
        filePaths_cameraPicture.add(f.getAbsolutePath());
        return f;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                listImageUri.add(Uri.parse("file://" + uri_cameraPicture));
            } else if (requestCode == SELECT_FILE) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        listImageUri.add(uri);
                    }
                } else {
                    Uri selectedImageUri = data.getData();
                    listImageUri.add(selectedImageUri);
                }
            }
            refreshImageToImageZone();
        }
    }

    private void refreshImageToImageZone(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.height = WindowUtil.dpToPixels(SaveMemoryActivity.this, 100);
        param.width = WindowUtil.dpToPixels(SaveMemoryActivity.this, 100);
        param.leftMargin = 5;
        param.rightMargin = 5;

        imageZone.removeAllViews();
        imageZone.addView(btn_addImage);

        if(listImageUri.size()!=0){
            for(int i = listImageUri.size()-1; i >= 0; i--){
                ImageView temp = new ImageView(this);
                temp.setLayoutParams(param);
                temp.setImageURI(listImageUri.get(i));
                final int finalI = i;
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(SaveMemoryActivity.this, DisplayImageActivity.class);
                        i.putExtra("uri",listImageUri.get(finalI).toString());
                        startActivity(i);
                    }
                });

                temp.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v) {
                        Uri temp = listImageUri.get(finalI);
                        listImageUri.remove(finalI);
                        for(int i = 0; i < uris_cameraPicture.size(); i++){
                            if(temp.equals(Uri.parse("file://" + uris_cameraPicture.get(i)))){
                                SystemUtil.deleteFile(filePaths_cameraPicture.get(i));
                            }
                        }

                        refreshImageToImageZone();
                        return true;
                    }
                });
                imageZone.addView(temp);
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yy = year;
        MM = month+1;
        dd = dayOfMonth;
        TimePickerDialog timePickerDialog = new TimePickerDialog(SaveMemoryActivity.this, SaveMemoryActivity.this, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String rs = yy+"/"+ DateTimeUtil.checkPickedNumber(MM)+"/"+DateTimeUtil.checkPickedNumber(dd)+" "+DateTimeUtil.checkPickedNumber(hourOfDay)+":"+DateTimeUtil.checkPickedNumber(minute)+":00";
        edt_createdAt.setText(rs);
    }

    public void onBackPressed (){
        for(int i = 0; i < uris_cameraPicture.size(); i++){
            SystemUtil.deleteFile(filePaths_cameraPicture.get(i));
        }
        animate2CloseIntent();
    }
}
