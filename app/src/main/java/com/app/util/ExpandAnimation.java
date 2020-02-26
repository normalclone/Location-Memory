package com.app.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandAnimation extends Animation {

    private int fromHeight;
    private int toHeight;
    private View view;

    public ExpandAnimation(View view, int fromHeight, int toHeight) {
        this.view = view;
        this.fromHeight = fromHeight;
        this.toHeight = toHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int newHeight;
        if (view.getHeight() != toHeight) {
            newHeight = (int) (fromHeight + ((toHeight - fromHeight) * interpolatedTime));
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        view.startAnimation(this);
    }
}

