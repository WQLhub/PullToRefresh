package com.example.lenovo.myapplication.PullToRefresh;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.lenovo.myapplication.R;

/**
 * Created by lenovo on 2016/12/19.
 */

public class PullLoadingScroll extends PullLoadingBaseView {

    private static final int MAX_NUM = 10;
    private LoadingLayout loadingLayout;
    private TextView tvLoading;
    private float divider = 7.0f;
    private ObjectAnimator rotateAnimator;


    public PullLoadingScroll(Context context, boolean isShowHeader) {
        super(context, isShowHeader);
    }

    public PullLoadingScroll(Context context, AttributeSet attrs, boolean isShowHeader) {
        super(context, attrs, isShowHeader);
    }

    public PullLoadingScroll(Context context, AttributeSet attrs) {
        super(context, attrs, true);
    }

    @Override
    protected void initView(Context context) {
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_scrolling,this);
        loadingLayout = (LoadingLayout) header.findViewById(R.id.pw_loading);
        tvLoading = (TextView) header.findViewById(R.id.tv_loading_tip);
        initAnim();
    }

    private void initAnim(){
        rotateAnimator = ObjectAnimator.ofFloat(loadingLayout,"rotation",0,360);
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
        if (!refreshingLabel.equals(tvLoading.getText())){
            tvLoading.setText(releaseLabel);
        }
    }

    @Override
    public void refreshing() {
        if (rotateAnimator!=null){
            loadingLayout.setProgress(0.8f);
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
    public void setCurrentView(int viewStatus, int marginTop) {
        int i = (int) ((viewStatus-marginTop)/divider);
        i = i>MAX_NUM?MAX_NUM:i;
        loadingLayout.setProgress(i/10.0f,true);
    }
}
