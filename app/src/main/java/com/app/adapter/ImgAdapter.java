package com.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.akshaykale.swipetimeline.ImageLoadingEngine;
import com.akshaykale.swipetimeline.TimelineFragment;
import com.akshaykale.swipetimeline.TimelineGroupType;
import com.akshaykale.swipetimeline.TimelineObject;
import com.akshaykale.swipetimeline.TimelineObjectClickListener;
import com.app.R;
import com.app.dao.ImgDAO;
import com.app.dao.MemoryDAO;
import com.app.fragment.ImagesFragment;
import com.app.model.Memory;
import com.app.model.Img;
import com.app.photoEditor.EditImageActivity;
import com.app.view.DisplayImageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.ViewHolder>{
    private Context mContext;
    private List<Img> list;
    private FragmentManager fragmentManager;
    private ImagesFragment fragment;

    public ImgAdapter(Context mContext, List<Img> list, FragmentManager fragmentManager, ImagesFragment fragment){
        this.mContext = mContext;
        this.list = list;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public ImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImgAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImgAdapter.ViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View view) {
            super(view);
        }

        public void bind(final ViewHolder holder, final int position) {
            TimelineFragment mFragment = new TimelineFragment();
            ArrayList<TimelineObject> objs = loadDataInTimeline();
            mFragment.setData(objs, TimelineGroupType.DAY);
            mFragment.setImageLoadEngine(new ImageLoadingEngine() {
                @Override
                public void onLoadImage(ImageView imageView, String uri) {
                    Picasso.Builder builder = new Picasso.Builder(mContext);

                    builder.listener(new Picasso.Listener()
                    {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                        {
                            exception.printStackTrace();
                        }
                    });
                    builder.build().load(uri).into(imageView);
                }
            });
            mFragment.addOnClickListener(new TimelineObjectClickListener() {
                @Override
                public void onTimelineObjectClicked(TimelineObject timelineObject) {
                    Intent i = new Intent(mContext, DisplayImageActivity.class);
                    i.putExtra("uri", timelineObject.getImageUrl());
                    mContext.startActivity(i);
                }

                @Override
                public void onTimelineObjectLongClicked(final TimelineObject timelineObject) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    final CharSequence[] items = {mContext.getString(R.string.edit_picture), mContext.getString(R.string.delete_picture), mContext.getString(R.string.cancel)};

                    alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if (items[i].equals(mContext.getString(R.string.edit_picture))) {
                                Intent intent = new Intent(mContext, EditImageActivity.class);
                                intent.putExtra("uri", timelineObject.getImageUrl());
                                intent.putExtra("id", new ImgDAO(mContext).getByLink(timelineObject.getImageUrl().substring(7)).getId());
                                mContext.startActivity(intent);
                                dialog.dismiss();
                            }
                            else if(items[i].equals(mContext.getString(R.string.delete_picture))){
                                new ImgDAO(mContext).delete(new ImgDAO(mContext).getByLink(timelineObject.getImageUrl().substring(7)).getId());
                                fragment.setAdapter();
                                dialog.dismiss();

                            }else{
                                dialog.dismiss();
                            }
                        }
                    });

                    alertDialog.create().show();
                }
            });

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container_img, mFragment);
            transaction.commit();
        }

        private ArrayList<TimelineObject> loadDataInTimeline() {
            //Load the data in a list and sort it by times in milli
            ArrayList<TimelineObject> objs = new ArrayList<>();
            for (Img i: list) {
                final Img temp = i;
                final Memory memory = new MemoryDAO(mContext).get(i.getMemory_id());
                TimelineObject obj = new TimelineObject() {
                    @Override
                    public long getTimestamp() {
                        return memory.getCreated_at().getTime();
                    }

                    @Override
                    public String getTitle() {
                        return memory.getTitle();
                    }

                    @Override
                    public String getImageUrl() {
                        return "file://"+temp.getLink();
                    }
                };
                objs.add(obj);
            }
            return objs;
        }
    }
}
