package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tuan800.tao800.R;

/**
 * Created by qjb on 2015/12/11.
 */
public abstract class PullLoadingBaseLayout extends FrameLayout {

    protected ImageView headerImage;
    protected ImageView sloganImage;
    protected ProgressBar headerProgress;
    protected TextView headerText;
    protected TextView refreshText;
    protected Animation rotateAnimation, resetRotateAnimation;

    //默认的字符串
    protected String releaseLabel = "松开刷新";
    protected String pullLabel = "下拉刷新";
    protected String refreshingLabel = "加载中...";
    protected String pullRefreshRedPacket = "松手有惊喜";//最好有默认文案
    protected boolean isShowHeader;
    protected ViewGroup header;

    public PullLoadingBaseLayout(Context context, final int mode, String releaseLabel, String pullLabel, String refreshingLabel, boolean isShowHeader) {
        super(context);
        if (!isShowHeader) {
            header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refreshs_header, this);
        } else {
            initView(context);

            this.releaseLabel = releaseLabel;
            this.pullLabel = pullLabel;
            this.refreshingLabel = refreshingLabel;
            if (headerImage != null) {
                this.headerImage.setImageResource(R.drawable.list_action_down);
            }
            this.isShowHeader = isShowHeader;
            switch (mode) {
                case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
                    if (headerImage != null) {
                        headerImage.setImageResource(R.drawable.list_action_up);
                    }
                    break;

                default:
                case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
                    if (headerImage != null) {
                        headerImage.setImageResource(R.drawable.list_action_down);
                    }
                    break;
            }
        }
    }

    public PullLoadingBaseLayout(Context context, boolean isShowHeader) {
        super(context);
        if (!isShowHeader) {
            header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refreshs_header, this);
        } else {
            initView(context);
            this.isShowHeader = isShowHeader;
        }
    }

    protected abstract void initView(Context context);

    //动画结束，重置列表状态
    public abstract void reset();

    //滑动超过指定headerHeader的情况
    public abstract void releaseToRefresh();

    public abstract void pullRefreshRedPacket();

    //列表正在刷新状态
    public abstract void refreshing();

    //下拉在指定高度之前调用
    public abstract void pull();

    //下拉到指定高度后，松手调用
    public abstract void pullToRefresh();

    public abstract void setCurrentView(int viewStatus, int initialMarginTop);

    public void setRefreshingLabel(String refreshingLabel) {
        this.refreshingLabel = refreshingLabel;
    }

    public void setReleaseLabel(String releaseLabel) {
        this.releaseLabel = releaseLabel;
    }

    public void setPullLabel(String pullLabel) {
        this.pullLabel = pullLabel;
    }

    public void setDefaultLabels(){
        releaseLabel = "松开刷新";
        pullLabel = "下拉刷新";
        refreshingLabel = "加载中...";
    }

    public void setPullRefreshRedPacket(String str){
        this.pullRefreshRedPacket = str;
    }
}
