package com.example.lenovo.myapplication.PullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.example.lenovo.myapplication.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by lenovo on 2016/12/19.
 */

public class GifLoadingLayout extends PullLoadingBaseView {
    private GifLoadingView mLoadingView;
    private GifImageView gifImageView;
    private GifDrawable gifDrawable;


    public GifLoadingLayout(Context context, boolean isShowHeader) {
        super(context, isShowHeader);
    }

    public GifLoadingLayout(Context context, AttributeSet attrs, boolean isShowHeader) {
        super(context, attrs, isShowHeader);
    }


    @Override
    protected void initView(Context context) {
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_gif,this);
        mLoadingView = (GifLoadingView) header.findViewById(R.id.pull_loading_view);
        gifImageView = (GifImageView) header.findViewById(R.id.pull_loading_gif_view);
        try {
            gifDrawable = new GifDrawable(getResources(),R.drawable.loadinggif);
            gifDrawable.setLoopCount(65530);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifImageView.setImageDrawable(gifDrawable);
    }

    @Override
    public void reset() {
        if (gifDrawable!=null){
            gifDrawable.stop();
        }
    }

    @Override
    public void releaseToRefresh() {

    }

    @Override
    public void refreshing() {
        mLoadingView.setVisibility(GONE);
        gifImageView.setVisibility(VISIBLE);
        gifDrawable.start();
    }

    @Override
    public void pull() {

    }

    @Override
    public void setCurrentView(int viewStatus, int marginTop) {
        gifImageView.setVisibility(GONE);
        mLoadingView.setVisibility(VISIBLE);
        mLoadingView.setCurrentView(viewStatus,marginTop);
        mLoadingView.invalidate();
    }
}
