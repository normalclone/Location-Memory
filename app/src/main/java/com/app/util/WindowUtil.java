package com.app.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.Toolbar;

public class WindowUtil {
    public static RelativeLayout.LayoutParams getToolBarParams(Context mContext, Toolbar toolbar){
        return  (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
    }

    public static int getStatusBarHeight(Context mContext){
        int id = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(id>0) return mContext.getResources().getDimensionPixelSize(id);
        return 0;
    }

    public static int getNavigationBarHeight(Context mContext){
        int id = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if(id>0) return mContext.getResources().getDimensionPixelSize(id);
        return 0;
    }

    public static double getViewY(View view){
        int[] lp = new int[2];
        view.getLocationOnScreen(lp);
        return lp[1];
    }

    public static double getViewX(View view){
        int[] lp = new int[2];
        view.getLocationOnScreen(lp);
        return lp[0];
    }

    public static void hideKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static Point getSceenSize(Activity activity){
        Point p = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(p);
        return p;
    }

    public static int dpToPixels(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
    }
}
