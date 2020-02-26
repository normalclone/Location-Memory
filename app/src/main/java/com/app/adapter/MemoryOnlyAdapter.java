package com.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.dao.ImgDAO;
import com.app.model.Img;
import com.app.model.Memory;
import com.app.util.CircleTransform;
import com.app.util.DateTimeUtil;
import com.app.view.MemoryDetailActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MemoryOnlyAdapter extends RecyclerView.Adapter<MemoryOnlyAdapter.ViewHolder> {
    private Context mContext;
    private List<Memory> list;

    public MemoryOnlyAdapter(Context mContext, List<Memory> list){
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MemoryOnlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemoryOnlyAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_memory_info_only, parent, false));
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
        ImageView iconProfile;
        TextView iconText;
        TextView tvTitle;
        TextView tvCreatedAt;
        CardView container;
        List<Img> listImg;
        public ViewHolder(View itemView) {
            super(itemView);

            iconProfile = itemView.findViewById(R.id.icon_profile);
            iconText = itemView.findViewById(R.id.icon_text);
            tvTitle = itemView.findViewById(R.id.tv_title_name);
            tvCreatedAt = itemView.findViewById(R.id.created_at);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final ViewHolder holder, final int position) {
            final Memory temp = list.get(position);
            listImg = new ImgDAO(mContext).getImgsByMemory(temp.getId());
            tvTitle.setText(temp.getTitle());
            tvCreatedAt.setText(DateTimeUtil.convertDatetimeToString(temp.getCreated_at()));
            holder.iconText.setText(temp.getTitle().substring(0, 1));

            applyProfilePicture(holder);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, MemoryDetailActivity.class);
                    i.putExtra("id", temp.getId());
                    mContext.startActivity(i);
                }
            });
        }

        private void applyProfilePicture(ViewHolder holder) {
            if (listImg.size() != 0) {
                Glide.with(mContext).load(listImg.get(0).getLink())
                        .thumbnail(0.5f)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(mContext)))
                        .into(holder.iconProfile);


                holder.iconProfile.setColorFilter(null);
                holder.iconText.setVisibility(View.GONE);
            } else {
                holder.iconProfile.setImageResource(R.drawable.circle);
                holder.iconText.setVisibility(View.VISIBLE);
            }
        }
    }
}