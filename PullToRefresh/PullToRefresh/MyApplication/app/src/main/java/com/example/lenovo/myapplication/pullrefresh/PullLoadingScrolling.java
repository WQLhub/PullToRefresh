package com.example.lenovo.myapplication.pullrefresh;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.tuan800.tao800.R;
import com.tuan800.tao800.home.components.ProgressWheel;

import static com.tuan800.tao800.R.id.tv_loading_tip;

/**
 * Created by qjb on 2016/8/4.
 */
public class PullLoadingScrolling extends PullLoadingBaseLayout{
    private static final int MAX_NUM = 10;
    private ProgressWheel pwLoading;
    private TextView tvLoading;
    private float switchPicDivider= 7.0f;
    private ObjectAnimator rotateAnimator;

    public PullLoadingScrolling(Context context, int mode, String releaseLabel, String pullLabel, String refreshingLabel, boolean isShowHeader) {
        super(context, mode, releaseLabel, pullLabel, refreshingLabel, isShowHeader);
    }

    public PullLoadingScrolling(Context context, int mode, boolean isShowHeader) {
        super(context, mode, "", "", "", isShowHeader);
    }

    public PullLoadingScrolling(Context context, boolean isShowHeader) {
        super(context,isShowHeader);
    }

    @Override
    protected void initView(Context context) {
        header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_scrolling, this);
        pwLoading = (ProgressWheel) header.findViewById(R.id.pw_loading);
        tvLoading = (TextView) header.findViewById(tv_loading_tip);
        initAnim();
    }

    private void initAnim(){
        rotateAnimator = ObjectAnimator.ofFloat(pwLoading,"rotation",0f,360f);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setDuration(1000);
        rotateAnimator.setRepeatCount(100);//超过超时时长即可
    }

    @Override
    public void reset() {
        if (rotateAnimator!=null){
            rotateAnimator.cancel();
        }
    }

    @Override
    public void releaseToRefresh() {
        if (!releaseLabel.equals(tvLoading.getText())){
            tvLoading.setText(releaseLabel);
        }
    }

    @Override//下拉到指定位置
    public void pullRefreshRedPacket() {
        tvLoading.setText(pullRefreshRedPacket);
    }

    @Override
    public void refreshing() {
        if (rotateAnimator!= null) {
            pwLoading.setProgress(0.8f);
            rotateAnimator.start();
            tvLoading.setText(refreshingLabel);
        }
    }

    @Override
    public void pull() {
        if (!pullLabel.equals(tvLoading.getText())){
            tvLoading.setText(pullLabel);
        }
    }

    @Override
    public void pullToRefresh() {
        tvLoading.setText(pullLabel);
    }

    @Override
    public void setCurrentView(int viewStatus, int initialMarginTop) {
        int i = (int) ((viewStatus - initialMarginTop) / switchPicDivider);
        if (i >= MAX_NUM) i = MAX_NUM;
        pwLoading.setProgress(i/10.0f,true);
    }
}
