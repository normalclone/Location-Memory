package com.app.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TransitionUtil {
    private static LinearLayout.LayoutParams fab1ParamTrueLocation;
    private static LinearLayout.LayoutParams fab2ParamTrueLocation;
    private static LinearLayout.LayoutParams fab3ParamTrueLocation;
    private static LinearLayout.LayoutParams fab4ParamTrueLocation;
    private static int leftMargin = 0;

    public static int singleSlideSpeed = 100;
    public static int fadeSpeed = 400;
    public static boolean isAtLeastLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static float getViewRadius(View view) {
        return (float) Math.hypot(view.getHeight() / 2, view.getWidth() / 2);
    }

    public static void hideNavigationModeFab(FloatingActionButton fab1, FloatingActionButton fab2, FloatingActionButton fab3, FloatingActionButton fab4){
        fab1ParamTrueLocation = (LinearLayout.LayoutParams) fab1.getLayoutParams();
        fab2ParamTrueLocation = (LinearLayout.LayoutParams) fab2.getLayoutParams();
        fab3ParamTrueLocation = (LinearLayout.LayoutParams) fab3.getLayoutParams();
        fab4ParamTrueLocation = (LinearLayout.LayoutParams) fab4.getLayoutParams();

        leftMargin = fab1ParamTrueLocation.leftMargin;

        fab1ParamTrueLocation.leftMargin = -fab1ParamTrueLocation.width -200 - fab1ParamTrueLocation.leftMargin;
        fab2ParamTrueLocation.leftMargin = -fab2ParamTrueLocation.width -200 - fab2ParamTrueLocation.leftMargin;
        fab3ParamTrueLocation.leftMargin = -fab3ParamTrueLocation.width -200 - fab3ParamTrueLocation.leftMargin;
        fab4ParamTrueLocation.leftMargin = -fab4ParamTrueLocation.width -200 - fab4ParamTrueLocation.leftMargin;
    }
    public static void navigationModeFabSlideIn(FloatingActionButton fab1, FloatingActionButton fab2, FloatingActionButton fab3, final FloatingActionButton fab4){
        ValueAnimator va4 = ValueAnimator.ofInt(fab4ParamTrueLocation.leftMargin, fab4ParamTrueLocation.width + leftMargin);
        va4.setDuration(singleSlideSpeed);
        va4.setInterpolator(new AccelerateDecelerateInterpolator());
        va4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ((LinearLayout.LayoutParams) fab4.getLayoutParams()).leftMargin = animatedValue;
                fab4.requestLayout();
            }
        });

        ValueAnimator va3 = createFabLeftSlideIn(fab3ParamTrueLocation, fab3, va4);
        ValueAnimator va2 = createFabLeftSlideIn(fab2ParamTrueLocation, fab2, va3);
        ValueAnimator va1 = createFabLeftSlideIn(fab1ParamTrueLocation, fab1, va2);

        va1.start();
    }

    public static void navigationModeFabSlideOut( FloatingActionButton fab1, FloatingActionButton fab2, FloatingActionButton fab3, final FloatingActionButton fab4){
        ValueAnimator va4 = ValueAnimator.ofInt(fab1ParamTrueLocation.leftMargin, -fab4ParamTrueLocation.width -200 - fab4ParamTrueLocation.leftMargin);
        va4.setDuration(singleSlideSpeed);
        va4.setInterpolator(new AccelerateDecelerateInterpolator());
        va4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ((LinearLayout.LayoutParams) fab4.getLayoutParams()).leftMargin = animatedValue;
                fab4.requestLayout();
            }
        });

        ValueAnimator va3 = createFabLeftSlideOut(fab2ParamTrueLocation, fab3, va4);
        ValueAnimator va2 = createFabLeftSlideOut(fab3ParamTrueLocation, fab2, va3);
        ValueAnimator va1 = createFabLeftSlideOut(fab4ParamTrueLocation, fab1, va2);

        va1.start();

    }

    public static ValueAnimator createFabLeftSlideIn(LinearLayout.LayoutParams params, final FloatingActionButton currentFab, final ValueAnimator nextVal){
        ValueAnimator va1 = ValueAnimator.ofInt(params.leftMargin, params.width + leftMargin);
        va1.setDuration(singleSlideSpeed);
        va1.setInterpolator(new AccelerateDecelerateInterpolator());
        va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ((LinearLayout.LayoutParams) currentFab.getLayoutParams()).leftMargin = animatedValue;
                currentFab.requestLayout();
                nextVal.start();
            }
        });
        return va1;
    }

    public static ValueAnimator createFabLeftSlideOut(LinearLayout.LayoutParams params, final FloatingActionButton currentFab, final ValueAnimator nextVal){
        ValueAnimator va1 = ValueAnimator.ofInt(params.leftMargin, -params.width -200 - params.leftMargin);
        va1.setDuration(singleSlideSpeed);
        va1.setInterpolator(new AccelerateDecelerateInterpolator());
        va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ((LinearLayout.LayoutParams) currentFab.getLayoutParams()).leftMargin = animatedValue;
                currentFab.requestLayout();
                nextVal.start();
            }
        });
        return va1;
    }

    public static void fabHalfFadeIn(final FloatingActionButton fab){
        fab.setAlpha(1f);
    }
    public static void fabHalfFadeOut(final FloatingActionButton fab){
        fab.setAlpha(0.4f);
    }

    public static AlphaAnimation fabFadeIn(final FloatingActionButton fab){
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(fadeSpeed);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return anim;
    }

    public static AlphaAnimation toolbarFadeIn(final Toolbar tb){
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(fadeSpeed);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return anim;
    }
}
