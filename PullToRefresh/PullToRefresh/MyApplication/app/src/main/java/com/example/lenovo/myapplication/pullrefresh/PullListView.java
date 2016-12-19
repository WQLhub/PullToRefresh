package com.example.lenovo.myapplication.pullrefresh;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.tuan800.tao800.share.components.SwipeListView;

//import com.tuan800.tao800.home.components.PullRefreshListViewBase;

/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 14-2-20
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class PullListView extends PullToRefreshAdapterViewBase<ListView> {
    public PullListView(Context context) {
        super(context);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullListView(Context context, int mode) {
        super(context, mode);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDisableScrollingWhileRefreshing(false);
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView lv = new SwipeListView(context, attrs);
//        final int mode = getMode();
//
//        pullLabel = context.getString(R.string.pull_to_refresh);
//        refreshingLabel = context.getString(R.string.label_loading);
//        releaseLabel = context.getString(R.string.pull_to_refresh_release);
//        upLabel = context.getString(R.string.up_to_refresh);
//
//        boolean isShowHeader;
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.pullshowheader);
//        isShowHeader = typedArray.getBoolean(R.styleable.pullshowheader_showheader, true);
//        if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH)&&isShowHeader) {
        FrameLayout frame = new FrameLayout(context);
//            mHeaderLoadingView = new PullingLayoutHome(context, MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel, refreshingLabel, isShowHeader);
//            frame.addView(mHeaderLoadingView, FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        frame.setVisibility(View.GONE);
        lv.addHeaderView(frame, null, false);//这个header是随着ListView一起的Header  这个长图是可以显示可以隐藏的那个头部
//        }
//        if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
//            mFooterLoadingView = new PullLoadingLayout(context, MODE_PULL_UP_TO_REFRESH, releaseLabel, upLabel, refreshingLabel, isShowHeader);
//            mFooterLoadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            mFooterLoadingView.setVisibility(View.GONE);
//        }

        lv.setId(android.R.id.list);
        return lv;
    }
}

