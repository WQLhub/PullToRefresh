package com.example.lenovo.myapplication.PullToRefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.lenovo.myapplication.R;

/**
 * Created by qiao on 2016/12/19.
 *
 * 专注于下拉刷新顶部view的实现
 *
 */

public abstract class PullToRefreshViewBase<T extends View> extends LinearLayout {

    protected Context mContext;

    protected int touchSlop;
    protected boolean isShowHeader;

    public T refreshableView;//下拉刷新的容器
    protected int loadingHeight;//loadingview的高度
    protected int headerLayoutHeight;//整个header的高度

    protected RelativeLayout headerLayout;
    protected PullLoadingBaseView loadingView;
    protected ImageView imageView;

    int imgWidth;
    int imgHeight;

    public PullToRefreshViewBase(Context context) {
        super(context);
        init(context,null);
    }

    public PullToRefreshViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    private void init(Context context,AttributeSet attrs){

        mContext = context;
        setOrientation(VERTICAL);
        touchSlop = ViewConfiguration.getTouchSlop();
        refreshableView = createRefreshableView(context);

        if (refreshableView!=null){
            addRefreshView(context,refreshableView);
        }

        if (attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.pullshowheader);
            isShowHeader = typedArray.getBoolean(R.styleable.pullshowheader_showheader,true);
            typedArray.recycle();
        }else {
            isShowHeader = true;//默认有下拉刷新的header
        }

        if (isShowHeader){
            loadingView = new PullLoadingScroll(context,true);
            measureView(loadingView);
            loadingHeight = loadingView.getMeasuredHeight();

            headerLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_scrolling,null);
            imageView = (ImageView) headerLayout.findViewById(R.id.pull_image);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.pull_image);

            headerLayout.addView(loadingView,layoutParams);
            addView(headerLayout,0,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            measureView(headerLayout);
            imgWidth = 1080;
            imgHeight = imgWidth*82/64;
            imageView.setLayoutParams(new ViewGroup.LayoutParams(imgWidth,imgHeight));
            headerLayoutHeight = imgHeight;
        }

        setPadding(0,-headerLayoutHeight,0,0);

    }

    public void addPullLoadingView(int what){
        headerLayout.removeAllViews();
        switch (what){
            case 1:
                loadingView = new PullLoadingScroll(mContext,true);
                break;
            case 2:

                break;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.pull_image);
        headerLayout.addView(loadingView,layoutParams);
    }

    private void measureView(View view){
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp==null){
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,0, lp.width);
        int lpHeight = lp.height;
        int childHeightSpec;
        if (lpHeight>0){
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
        }else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec,childHeightSpec);
    }

    protected abstract T createRefreshableView(Context context);

    private void addRefreshView(Context context,T view){
        addView(refreshableView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1.0f));
    }

}
