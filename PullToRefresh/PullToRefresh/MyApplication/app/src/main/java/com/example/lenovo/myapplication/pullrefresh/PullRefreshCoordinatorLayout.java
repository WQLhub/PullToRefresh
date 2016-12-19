package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

/**
 * Created by lenovo on 2016/10/29.
 */

public class PullRefreshCoordinatorLayout extends PullToRefreshBase<CoordinatorLayout> {

    public PullRefreshCoordinatorLayout(Context context) {
        super(context);
    }

    public PullRefreshCoordinatorLayout(Context context, int mode) {
        super(context, mode);
    }

    public PullRefreshCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CoordinatorLayout createRefreshableView(Context context, AttributeSet attrs) {
        return new CoordinatorLayout(context, attrs);
    }

    boolean isReadyForPullDown = false;
    public void setReadyForPullDown(boolean isReadyForPullDown){
        this.isReadyForPullDown = isReadyForPullDown;
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isReadyForPullDown;
    }

    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    public boolean isLastItemVisible() {
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            //这里这样的. 布局文件上写的是自定义的PullRefreshRecyclyerView.
            // 而真正视图上存在的是reclyerView
            //当app被杀掉的时候,有的手机9300 按照 xml进行恢复, 而这两者是不同的.
            // 一个是view  一个 是recyclyerView
            if (state instanceof Bundle) {
                Bundle bundle = (Bundle) state;
                state = bundle.getParcelable("superState");
            }
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
