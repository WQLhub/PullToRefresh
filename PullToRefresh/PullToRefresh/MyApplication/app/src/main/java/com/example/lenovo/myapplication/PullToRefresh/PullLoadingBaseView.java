package com.example.lenovo.myapplication.PullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by lenovo on 2016/12/19.
 */

public abstract class PullLoadingBaseView extends FrameLayout {

    protected String releaseLabel = "松开刷新";
    protected String pullLabel = "下拉刷新";
    protected String refreshingLabel = "加载中...";
    protected boolean isShowHeader;
    protected View header;

    public PullLoadingBaseView(Context context,boolean isShowHeader) {
        super(context);
        init(isShowHeader);
        if (isShowHeader){
            initView(context);
        }
    }

    public PullLoadingBaseView(Context context, AttributeSet attrs,boolean isShowHeader) {
        super(context, attrs);
        init(isShowHeader);
        if (isShowHeader){
            initView(context);
        }
    }

    private void init(boolean isShowHeader){
        this.isShowHeader = isShowHeader;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public void setReleaseLabel(String releaseLabel){
        this.releaseLabel = releaseLabel;
    }

    public void setPullLabel(String pullLabel){
        this.pullLabel = pullLabel;
    }

    public void setRefreshingLabel(String refreshingLabel){
        this.refreshingLabel = refreshingLabel;
    }

    //初始化视图的View；
    protected abstract void initView(Context context);

    //重置各种状态
    public abstract void reset();

    //超过指定headerHeight的情况
    public abstract void releaseToRefresh();

    //列表正在刷新的状态
    public abstract void refreshing();

    //下拉再指定高度后，松手调用
    public abstract void pull();

    //类似帧动画效果
    public abstract void setCurrentView(int viewStatus,int marginTop);

}
