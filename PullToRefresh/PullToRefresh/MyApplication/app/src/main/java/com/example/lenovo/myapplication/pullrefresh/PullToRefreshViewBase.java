package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tuan800.tao800.R;
import com.tuan800.zhe800.framework.app.devinfo.ScreenUtil;
import com.tuan800.zhe800.framework.develop.LogUtil;
import com.tuan800.zhe800.framework.image.glide.util.ImageLoadUtil;
import com.tuan800.zhe800.framework.util.StringUtil;

/**
 * Created by qjb on 2016/12/14.
 *
 * 下拉刷新相关的视图初始化处理
 *
 */

public abstract class PullToRefreshViewBase<T extends View> extends LinearLayout {

    //当前mode状态  view状态
    public static final int MANUAL_REFRESHING = 0x3;
    public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
    public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
    public static final int MODE_BOTH = 0x3;
    public static final int MODE_DISABLE = 0X4;

    protected Context mContext;
    protected int touchSlop;

    protected boolean isShowHeader;

    protected int mode = MODE_BOTH;
    protected int currentMode;

    public T refreshableView;
    protected int headerHeight;
    protected int headerImageHeight;
    protected PullLoadingBaseLayout headerLayout;
    protected RelativeLayout mHeaderImageView;
    protected ImageView imageView;
    int width;//加载下来的图片的宽度
    int height;//加载下来图片的高度

    public PullToRefreshViewBase(Context context) {
        super(context);
        init(context,null);
    }

    public PullToRefreshViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        touchSlop = ViewConfiguration.getTouchSlop();

        refreshableView = this.createRefreshableView(context, attrs);
        if (refreshableView!=null){
            this.addRefreshableView(context, refreshableView);
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.pullshowheader);
        isShowHeader = typedArray.getBoolean(R.styleable.pullshowheader_showheader, true);
        typedArray.recycle();
        if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) && isShowHeader) {
            //正在刷新的头部
            headerLayout = new PullingLayoutHome(context, MODE_PULL_DOWN_TO_REFRESH, isShowHeader);
            measureView(headerLayout);
            headerHeight = headerLayout.getMeasuredHeight();

            //多点触控的头部
            mHeaderImageView = null;
            mHeaderImageView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.header_pulltorefresh_home, null);
            imageView = (ImageView) mHeaderImageView.findViewById(R.id.pull_image);
            addView(mHeaderImageView, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.pull_image);
            mHeaderImageView.addView(headerLayout,1,layoutParams);

            measureView(mHeaderImageView);
            setGravity(Gravity.CENTER_HORIZONTAL);
            width = ScreenUtil.WIDTH;
            height = width * 820 / 640;
            mHeaderImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            headerImageHeight = height;
        }

        switch (mode) {
            case MODE_BOTH:
            case MODE_PULL_DOWN_TO_REFRESH:
                setPadding(0, - headerImageHeight, 0, 0);//隐藏掉头部的内容
                break;
        }
        if (mode != MODE_BOTH) {
            currentMode = mode;
        }

        if (mHeaderImageView != null) {
            mHeaderImageView.setVisibility(View.VISIBLE);
        }
    }

    public void setPullImage(String imgUrl, boolean isHome){
        if (null == imageView)
            return;
        if (isHome){
            if (StringUtil.isEmpty(imgUrl)){
                addPullHeaderView(1,imgUrl);
            }else {
                addPullHeaderView(2,imgUrl);
                ImageLoadUtil.getInstance().loadImageHolder(getContext(), imageView, imgUrl, R.drawable.bg_pull_img_default, R.drawable.bg_pull_img_default);
            }
        }else {
            addPullHeaderView(1,imgUrl);
        }
    }

    public void addPullHeaderView(int whichHeader,String imageUrl){
        switch (whichHeader){
            case 1://划箱子的header
                if (headerLayout==null){//正常情况下
                    headerLayout = new PullingLayoutHome(mContext, MODE_PULL_DOWN_TO_REFRESH, isShowHeader);
                }
                break;
            case 2://画圆环的header
                if (headerLayout==null){//正常情况下
                    headerLayout = new PullLoadingScrolling(mContext,isShowHeader);
                }else {//异常  并且imageurl空   这个时候要重新去实例化这个headerLayout
                    if (!StringUtil.isEmpty(imageUrl)&&headerLayout instanceof PullingLayoutHome){
                        LogUtil.d("qjb-test addPull headerLayout 1:"+headerLayout.toString());
                        mHeaderImageView.removeView(headerLayout);
                        headerLayout = new PullLoadingScrolling(mContext,isShowHeader);
                        LogUtil.d("qjb-test addPull headerLayout 2:"+headerLayout.toString());
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.pull_image);
                        mHeaderImageView.addView(headerLayout,1,layoutParams);
                    }
                }
                break;
        }
        if (headerLayout!=null){
            measureView(headerLayout);
            headerHeight = headerLayout.getMeasuredHeight();
            if (headerLayout.getParent()==null){
                measureView(headerLayout);
                headerHeight = headerLayout.getMeasuredHeight();
                switch (mode) {
                    case MODE_BOTH:
                    case MODE_PULL_DOWN_TO_REFRESH:
                        setPadding(0, - headerImageHeight, 0, 0);//隐藏掉头部的内容
                        break;
                }
                if (mode != MODE_BOTH) {
                    currentMode = mode;
                }
            }
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * This is implemented by derived classes to return the created View. If you
     * need to use a custom View (such as a custom ListView), override this
     * method and return an instance of your custom class.
     * <p/>
     * Be sure to set the ID of the view in this method, especially if you're
     * using a ListActivity or ListFragment.
     *
     * @param context
     * @param attrs   AttributeSet from wrapped class. Means that anything you
     *                include in the XML layout declaration will be routed to the
     *                created View
     * @return New instance of the Refreshable View
     */
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    public void addRefreshableView(Context context, T refreshableView) {
        addView(refreshableView, new LayoutParams(LayoutParams.FILL_PARENT, 0, 1.0f));
    }

    /**
     * Deprecated. Use {@link #getRefreshableView()} from now on.
     *
     * @return The Refreshable View which is currently wrapped
     * @deprecated
     */
    public final T getAdapterView() {
        return refreshableView;
    }

    /**
     * Get the Wrapped Refreshable View. Anything returned here has already been
     * added to the content view.
     *
     * @return The View which is currently wrapped
     */
    public final T getRefreshableView() {
        return refreshableView;
    }
}
