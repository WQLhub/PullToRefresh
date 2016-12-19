package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.tuan800.zhe800.list.containers.SwipeRecyclerView;

/**
 * Created by qjb on 2016/4/8.
 */
public class PullRefreshRecyclerView<T extends View> extends PullToRefreshBase<SwipeRecyclerView> {
    public PullRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullRefreshRecyclerView(Context context, int mode) {
        super(context, mode);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected SwipeRecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        return new SwipeRecyclerView(context, attrs);
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    public boolean isFirstItemVisible() {
        int posititon = 0;
        RecyclerView.LayoutManager manager = refreshableView.getLayoutManager();
        posititon = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        int itemCount = this.refreshableView.getAdapter().getItemCount()+refreshableView.getHeaderViewCount();
        if (itemCount == 0) {
            return true;

        }

        //这个破东西太麻烦了；当header添加到列表的时候，当header隐藏时，findFirstVisibleItemPosition()==1，当
        //heaader显示的时候，header不存在的时候，findFirstVisibleItemPosition()==0
        //header隐藏存在两种情况：1、直接设置为gone   2、是高度很低
        View view = refreshableView.getHeaderView();
        if (view!=null&&(view.getMeasuredHeight()<10||view.getVisibility()==GONE)){
            if (posititon == 1) {
                final View firstVisibleChild = refreshableView.getChildAt(1);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= refreshableView.getTop() - 2;//因为scrollBy(1,1);
                }
            }
        }

        if (posititon == 0 || posititon == -1) {
            final View firstVisibleChild = refreshableView.getChildAt(0);
            if (firstVisibleChild != null) {
                return firstVisibleChild.getTop() >= refreshableView.getTop()-2;//因为scrollBy(1,1);
            }
        }

        return false;
    }

    public boolean isLastItemVisible() {
        return false;
    }

    public void setHeadRefresh(boolean doScroll) {
        RecyclerView.Adapter adapter = refreshableView.getAdapter();
        if (!getShowViewWhileRefreshing() || null == adapter || adapter.getItemCount() == 0) {
            super.setRefreshingInternal(doScroll);
            return;
        }

        refreshableView.scrollToPosition(0);
        super.setRefreshingInternal(doScroll);
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
