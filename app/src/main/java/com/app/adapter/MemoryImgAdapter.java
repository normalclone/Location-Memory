package com.app.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.dao.ImgDAO;
import com.app.model.Img;
import com.app.photoEditor.EditImageActivity;
import com.app.view.MemoryDetailActivity;

import java.util.List;

public class MemoryImgAdapter extends RecyclerView.Adapter<MemoryImgAdapter.ViewHolder> {
    private Context mContext;
    private List<Img> list;

    public MemoryImgAdapter(Context mContext, List<Img> list){
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MemoryImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemoryImgAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_image_memory, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        Button btn_delete;
        Button btn_edit;
        public ViewHolder(View itemView) {
            super(itemView);
            btn_delete = itemView.findViewById(R.id.delete_img);
            btn_edit = itemView.findViewById(R.id.edit_img);
            imageView = itemView.findViewById(R.id.imgView);
        }

        public void bind(final ViewHolder holder, final int position) {
            final Img temp = list.get(position);
            imageView.setImageURI(Uri.parse("file://"+temp.getLink()));
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, EditImageActivity.class);
                    i.putExtra("uri", "file://"+temp.getLink());
                    i.putExtra("id", temp.getId());
                    mContext.startActivity(i);
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setTitle(mContext.getString(R.string.notice));
                    alertDialog.setMessage(mContext.getString(R.string.are_you_sure));
                    alertDialog.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(new ImgDAO(mContext).delete(temp.getId())){
                                list.remove(temp);
                                Toast.makeText(mContext, mContext.getString(R.string.success), Toast.LENGTH_LONG).show();
                                notifyDataSetChanged();
                            }else{
                                Toast.makeText(mContext, mContext.getString(R.string.failure), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alertDialog.setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog dialog = alertDialog.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        AlertDialog d = dialog;
                        @Override
                        public void onShow(DialogInterface dia) {
                            d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor("#ffffff"));
                            d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#ffffff"));
                            d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                            d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                        }
                    });
                    dialog.show();
                }
            });
        }
    }
}