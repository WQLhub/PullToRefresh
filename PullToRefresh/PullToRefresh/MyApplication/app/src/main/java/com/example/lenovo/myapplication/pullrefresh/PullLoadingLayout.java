package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tuan800.tao800.R;

/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 12-3-22
 * Time: 上午11:08
 * To change this template use File | SettingsActivity | File Templates.
 */
public class PullLoadingLayout extends PullLoadingBaseLayout {

    private static final int DEFAULT_ROTATION_ANIMATION_DURATION = 400;

    public PullLoadingLayout(Context context, int mode, String releaseLabel, String pullLabel, String refreshingLabel, boolean isShowHeader) {
        super(context, mode, releaseLabel, pullLabel, refreshingLabel, isShowHeader);
    }

    @Override
    protected void initView(Context context) {
        header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
        headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
        sloganImage = (ImageView) header.findViewById(R.id.img_slogan);
        headerImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
        headerProgress = (ProgressBar) header.findViewById(R.id.pull_to_refresh_progress);
        refreshText = (TextView) header.findViewById(R.id.tv_refresh_text);

        final Interpolator interpolator = new LinearInterpolator();
        rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(interpolator);
        rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setDuration(100);

        resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        resetRotateAnimation.setInterpolator(interpolator);
        resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        resetRotateAnimation.setFillAfter(true);

    }

    //当PullSwipeListView的不带头部的属性设置了之后，slognImage为null，应return；
    public void reset() {
        if (!isShowHeader) return;
        headerText.setText(pullLabel);
        headerText.setVisibility(VISIBLE);
        headerImage.setVisibility(View.VISIBLE);
        refreshText.setVisibility(GONE);
        headerProgress.setVisibility(View.GONE);
        sloganImage.setVisibility(View.VISIBLE);
    }

    public void releaseToRefresh() {
        if (!isShowHeader) return;
        refreshText.setVisibility(GONE);
        headerText.setText(releaseLabel);
        headerText.setVisibility(VISIBLE);
        headerImage.clearAnimation();
        headerImage.startAnimation(rotateAnimation);
        sloganImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void pullRefreshRedPacket() {

    }

    //header 和 footer的正在刷新时一样的
    public void refreshing() {
        if (!isShowHeader) return;
        refreshText.setVisibility(VISIBLE);
        headerText.setVisibility(INVISIBLE);
        headerImage.clearAnimation();
        headerProgress.setVisibility(View.VISIBLE);
        headerImage.setVisibility(View.INVISIBLE);
        sloganImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void pull() {

    }

    public void pullToRefresh() {
        if (!isShowHeader) return;
        headerText.setText(pullLabel);
        headerText.setVisibility(VISIBLE);
        headerImage.clearAnimation();
        refreshText.setVisibility(GONE);
        headerImage.startAnimation(resetRotateAnimation);
        sloganImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCurrentView(int viewStatus, int initialMarginTop) {

    }
}