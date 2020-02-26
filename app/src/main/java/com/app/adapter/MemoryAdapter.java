package com.app.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.dao.ImgDAO;
import com.app.dao.tLocationDAO;
import com.app.model.Img;
import com.app.model.Memory;
import com.app.util.CircleTransform;
import com.app.util.DateTimeUtil;
import com.app.util.ExpandAnimation;
import com.app.util.FlipAnimator;
import com.app.util.WindowUtil;
import com.app.view.DisplayImageActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {

    private OnClickListener onClickListener;
    private List<Memory> memoryList;
    private Context mContext;
    private SparseBooleanArray selectedItems;
    private List<Img> listImg;

    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    private static int currentSelectedIndex = -1;

    public MemoryAdapter(Context mContext , List<Memory> memoryList , OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.memoryList = memoryList;
        this.mContext = mContext;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        listImg = new ArrayList<Img>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_memory_info, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        memoryList.remove(position);
        resetCurrentIndex();
    }
    public long getItemId(int position) {
        return memoryList.get(position).getId();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        View circleView;
        View expandView;
        View expandableView;

        CardView memoryContainer;
        RelativeLayout iconBack, iconFront;
        TextView title, locationName ,createdAt, iconText;

        ImageView iconProfile;
        LinearLayout imgZone;

        boolean expanded;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            expandView = itemView.findViewById(R.id.expandView);
            expandableView = itemView.findViewById(R.id.expandableView);
            circleView = itemView.findViewById(R.id.circleView);

            title = itemView.findViewById(R.id.tv_title_name);
            locationName = itemView.findViewById(R.id.tv_location_name);

            iconProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            iconText = (TextView) itemView.findViewById(R.id.icon_text);
            iconBack = (RelativeLayout) itemView.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) itemView.findViewById(R.id.icon_front);
            memoryContainer = (CardView) itemView.findViewById(R.id.container);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            imgZone = (LinearLayout) itemView.findViewById(R.id.imgs_zone);
            circleView.setOnClickListener(this);
        }

        public void bind(ViewHolder holder,int position) {
            Memory current = memoryList.get(position);
            title.setText(current.getTitle());
            locationName.setText(new tLocationDAO(mContext).get(current.getLocation_id()).getLocationName());
            createdAt.setText(DateTimeUtil.convertDatetimeToString(current.getCreated_at()));
            holder.iconText.setText(current.getTitle().substring(0, 1));
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            param.height = WindowUtil.dpToPixels(mContext, 120);
            param.width = WindowUtil.dpToPixels(mContext, 120);
            param.leftMargin = 5;
            param.rightMargin = 5;
            listImg = new ImgDAO(mContext).getImgsByMemory(current.getId());
            for (Img i: listImg) {
                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(param);
                imageView.setImageURI(Uri.parse(i.getLink()));
                final String finalUri = i.getLink();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, DisplayImageActivity.class);
                        intent.putExtra("uri",finalUri);
                        mContext.startActivity(intent);
                    }
                });

                imgZone.addView(imageView);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleView.setTransitionName("transition" + position);
                circleView.invalidate();
            }
            ViewGroup.LayoutParams params = expandView.getLayoutParams();
            params.height = expanded ?
                    itemView.getResources().getDimensionPixelOffset(R.dimen.card_expanded_height)
                    : itemView.getResources().getDimensionPixelOffset(R.dimen.card_default_height);
            expandView.setLayoutParams(params);
            expandableView.setAlpha(expanded ? 1.0f : 0.0f);

            applyIconAnimation(holder, position);
            applyProfilePicture(holder);
            applyClickEvents(holder, position);

        }

        private void applyClickEvents(ViewHolder holder, final int position) {

            holder.memoryContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onClickListener.onLongClick(position);
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return true;
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

        private void applyIconAnimation(ViewHolder holder, int position) {
            if (selectedItems.get(position, false)) {
                holder.iconFront.setVisibility(View.GONE);
                resetIconYAxis(holder.iconBack);
                holder.iconBack.setVisibility(View.VISIBLE);
                holder.iconBack.setAlpha(1);
                if (currentSelectedIndex == position) {
                    FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                    resetCurrentIndex();
                }
            } else {
                holder.iconBack.setVisibility(View.GONE);
                resetIconYAxis(holder.iconFront);
                holder.iconFront.setVisibility(View.VISIBLE);
                holder.iconFront.setAlpha(1);
                if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                    FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                    resetCurrentIndex();
                }
            }
        }

        private void resetIconYAxis(View view) {
            if (view.getRotationY() != 0) {
                view.setRotationY(0);
            }
        }

        private void resetCurrentIndex() {
            currentSelectedIndex = -1;
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.circleView || v.getId() == R.id.icon_front || v.getId() == R.id.icon_back) {
                if (onClickListener != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        onClickListener.onItemClick(v, v.getTransitionName(), getAdapterPosition());
                    } else {
                        onClickListener.onItemClick(v, "", getAdapterPosition());
                    }
                }
            } else {
                expandOrCollapse();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            onClickListener.onLongClick(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }

        private void expandOrCollapse() {
            int expandedHeight = itemView.getResources()
                    .getDimensionPixelOffset(R.dimen.card_expanded_height);
            int defaultHeight = itemView.getResources()
                    .getDimensionPixelOffset(R.dimen.card_default_height);

            if (expanded) {
                int tmp = defaultHeight;
                defaultHeight = expandedHeight;
                expandedHeight = tmp;
            }

            ExpandAnimation expandAnimation = new ExpandAnimation(expandView, defaultHeight,
                    expandedHeight);
            expandAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            expandAnimation.setDuration(375);
            expandAnimation.start();

            expandableView.animate().alpha(expanded ? 0.0f : 1.0f).setStartDelay(75);
            expanded = !expanded;
        }
    }

    public interface OnClickListener {
        void onItemClick(View sharedView, String transitionName, int position);

        void onLongClick(int position);
    }
}
