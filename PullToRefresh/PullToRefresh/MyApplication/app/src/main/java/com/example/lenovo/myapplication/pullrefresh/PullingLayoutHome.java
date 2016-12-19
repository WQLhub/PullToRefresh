package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuan800.tao800.R;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by qjb on 2015/12/10.
 */

public class PullingLayoutHome extends PullLoadingBaseLayout {
    private PullLoadingViewHome pullLoadingViewHome;
    private GifImageView gifImageView;
    private pl.droidsonroids.gif.GifDrawable gifDrawable;

    public PullingLayoutHome(Context context, int mode, String releaseLabel, String pullLabel, String refreshingLabel, boolean isShowHeader) {
        super(context, mode, releaseLabel, pullLabel, refreshingLabel, isShowHeader);
    }

    public PullingLayoutHome(Context context, int mode, boolean isShowHeader) {
        super(context, mode, "", "", "", isShowHeader);
    }

    @Override
    protected void initView(Context context) {
        header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_home_header, this);
        pullLoadingViewHome = (PullLoadingViewHome) header.findViewById(R.id.pull_loading_view);
        gifImageView = (GifImageView) header.findViewById(R.id.pull_loading_gif_view);
        try {
            gifDrawable = new pl.droidsonroids.gif.GifDrawable(context.getAssets(), "pull_refresh_second.gif");
            gifDrawable.setLoopCount(65530);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gifImageView.setImageDrawable(gifDrawable);
    }

    @Override
    public void reset() {
        if(gifDrawable != null){
            gifDrawable.stop();
        }
    }

    @Override
    public void releaseToRefresh() {
    }

    @Override
    public void pullRefreshRedPacket() {

    }

    @Override
    public void refreshing() {
        pullLoadingViewHome.setVisibility(View.GONE);
        try {
            gifDrawable = new pl.droidsonroids.gif.GifDrawable(getContext().getAssets(), "pull_refresh_second.gif");
            gifDrawable.setLoopCount(65530);
            gifImageView.setImageDrawable(gifDrawable);
            gifImageView.setVisibility(VISIBLE);
            gifDrawable.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull() {

    }

    @Override
    public void pullToRefresh() {
    }

    @Override
    public void setCurrentView(int viewStatus, int initialMarginTop) {
        if (gifImageView.getVisibility() != View.GONE) {
            gifImageView.setVisibility(View.GONE);
        }
        if (pullLoadingViewHome.getVisibility() != View.VISIBLE) {
            pullLoadingViewHome.setVisibility(View.VISIBLE);
        }
        if (pullLoadingViewHome != null) {
            pullLoadingViewHome.setCurrentView(viewStatus, initialMarginTop);
            pullLoadingViewHome.postInvalidate();
        }
    }
}
