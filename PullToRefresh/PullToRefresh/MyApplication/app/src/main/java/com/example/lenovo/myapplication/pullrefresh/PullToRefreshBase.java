package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.tuan800.tao800.R;
import com.tuan800.zhe800.framework.app.IntentBundleFlag;
import com.tuan800.zhe800.framework.app.Tao800Application;
import com.tuan800.zhe800.framework.store.DB.beans.Preferences;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qjb on 2016/12/14.
 *
 * 下拉相关滑动的处理
 *
 */
public abstract class PullToRefreshBase<T extends View> extends PullToRefreshViewBase<T> {

    //滑动相关状态
    protected static final float FRICTION = 2.0f;
    protected static final int PULL_TO_REFRESH = 0x0;
    protected static final int RELEASE_TO_REFRESH = 0x1;
    protected static final int REFRESHING = 0x2;
    protected static final int RELEASE_TO_RECOVER = 0x7;//整个屏幕恢复到下拉之前的位置
    protected int state = PULL_TO_REFRESH;

    private boolean isSupportedRedPacket = false;
    private static final int recoverHeight;
    private OnRedPacketRefreshListener redPacketRefreshListener;

    private static final SimpleDateFormat DISPLAY_DATE_FORMAT;

    static {
        DISPLAY_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");
        recoverHeight = Tao800Application.getInstance().getResources().getDimensionPixelOffset(R.dimen.redpacket_pull_refresh_height);
    }

    private final Handler handler = new Handler();
    public boolean isMargeTop = false;


    protected boolean isSupportMultiPull = true;
    // 判断点下的是那跟手指
    private int mode_multi = -1;
    int pullMarginTop[] = new int[2];
    int initalMarginTop = 0;

    private float mLastTouchY;
    private boolean mFlingUp;
    private float initialMotionY;
    private float lastMotionX;
    private float lastMotionY;
    private boolean isBeingDragged = false;
    private boolean isPullToRefreshEnabled = true;
    private boolean disableScrollingWhileRefreshing = true;
    private boolean showViewWhileRefreshing = true;

    private SmoothScrollRunnable currentSmoothScrollRunnable;
    private OnRefreshListener onRefreshListener;
    private float deltY = 0;
    private float multiStartY = 0;
    private float multiLastStartY = 0;
    private float archY = 0;
    private boolean isMultiScrolling = false;

    public PullToRefreshBase(Context context) {
        super(context);
    }

    public PullToRefreshBase(Context context, int mode) {
        super(context);
        this.mode = mode;
    }

    public PullToRefreshBase(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    public final boolean isFlingUp() {
        return mFlingUp;
    }

    /**
     * Get whether the 'Refreshing' View should be automatically shown when
     * refreshing. Returns true by default.
     *
     * @return - true if the Refreshing View will be show
     */
    public final boolean getShowViewWhileRefreshing() {
        return showViewWhileRefreshing;
    }

    /**
     * Whether Pull-to-Refresh is enabled
     *
     * @return enabled
     */
    public final boolean isPullToRefreshEnabled() {
        return isPullToRefreshEnabled;
    }

    /**
     * A mutator to enable/disable Pull-to-Refresh for the current View
     *
     * @param enable Whether Pull-To-Refresh should be used
     */
    public final void setPullToRefreshEnabled(boolean enable) {
        this.isPullToRefreshEnabled = enable;
    }

    /**
     * Returns whether the widget has disabled scrolling on the Refreshable View
     * while refreshing.
     */
    public final boolean isDisableScrollingWhileRefreshing() {
        return disableScrollingWhileRefreshing;
    }

    /**
     * By default the Widget disabled scrolling on the Refreshable View while
     * refreshing. This method can change this behaviour.
     *
     * @param disableScrollingWhileRefreshing - true if you want to disable scrolling while refreshing
     */
    public final void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
        this.disableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
    }

    /**
     * Returns whether the Widget is currently in the Refreshing state
     *
     * @return true if the Widget is currently refreshing
     */
    public final boolean isRefreshing() {
        return state == REFRESHING || state == MANUAL_REFRESHING;
    }

    /**
     * Sets the Widget to be in the refresh state. The UI will be updated to
     * show the 'Refreshing' view.
     * 列表正在刷新的时候显示的状态
     *
     * @param doScroll - true if you want to force a scroll to the Refreshing view.
     */
    public final void setRefreshing(boolean doScroll) {
        if (!isRefreshing()) {
            setRefreshingInternal(doScroll);
            state = MANUAL_REFRESHING;
        }
    }

    /**
     * Mark the current Refresh as complete. Will Reset the UI and hide the
     * Refreshing View
     */
    public final void onRefreshComplete() {
        if (state != PULL_TO_REFRESH) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    resetHeader();
                }
            });
        }
    }

    /**
     * 下拉动画去掉延迟
     */
    public final void onNoDelayRefreshComplete() {
        if (state != PULL_TO_REFRESH) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    resetHeader();
                }
            });
        }
    }

    /**
     * Set OnRefreshListener for the Widget
     *
     * @param listener - Listener to be used when the Widget is set to Refresh
     */
    public final void setOnRefreshListener(OnRefreshListener listener) {
        onRefreshListener = listener;
    }

    /**
     * Set Text to show when the Widget is being pulled, and will refresh when
     * released
     * 设置刷新之后，松手显示的文案；
     *
     * @param releaseLabel - String to display
     */
    public void setReleaseLabel(String releaseLabel) {
        if (null != headerLayout) {
            headerLayout.setReleaseLabel(releaseLabel);
        }
    }

    //设置下拉刷新红包位置的字符串
    public void setPullRefreshRedPacket(String label) {
        if (null != headerLayout) {
            headerLayout.setPullRefreshRedPacket(label);
        }
    }

    //是否支持红包
    public void setIsSupportedRedPacket(boolean isSupportedRedPacket){
        this.isSupportedRedPacket = isSupportedRedPacket;
    }

    /**
     * Set Text to show when the Widget is being Pulled
     * 设置下拉时显示的文案；
     *
     * @param pullLabel - String to display
     */
    public void setPullLabel(String pullLabel) {
        if (null != headerLayout) {
            headerLayout.setPullLabel(pullLabel);
        }
    }

    public void recoverLabels(){
        if (null != headerLayout) {
            headerLayout.setDefaultLabels();
        }
    }

    /**
     * Set Text to show when the Widget is refreshing
     * 设置正在刷新时显示的文案
     *
     * @param refreshingLabel - String to display
     */
    public void setRefreshingLabel(String refreshingLabel) {
        if (null != headerLayout) {
            headerLayout.setRefreshingLabel(refreshingLabel);
        }
    }

    public final void setHeaderRefreshing() {
        if (!isRefreshing()) {
            state = REFRESHING;
            if (null != headerLayout) {
                headerLayout.refreshing();
            }
            smoothScrollTo(-headerHeight);
            state = MANUAL_REFRESHING;
        }
    }

    public final boolean hasPullFromTop() {
        return currentMode != MODE_PULL_UP_TO_REFRESH;
    }

    /**
     * Set Time to show when the Widget is refreshed
     * 设置更新时间，QQ有这个
     *
     * @param isRefreshComplete
     */
    public void setRefreshedTimeLabel(boolean isRefreshComplete) {
        StringBuilder updateAt = new StringBuilder();
        updateAt.append(mContext.getString(R.string.pull_update_time));
        if (isRefreshComplete) {
            String updateTime = DISPLAY_DATE_FORMAT.format(new Date());
            updateAt.append(updateTime);
            Preferences.getInstance().save(IntentBundleFlag.RECOM_UPDATE_TIME, updateTime);
        } else {
            updateAt.append(Preferences.getInstance().get(IntentBundleFlag.RECOM_UPDATE_TIME));
        }

    }

    /**
     * 重置Header，恢复下拉之前的状态
     */
    protected void resetHeader() {
        state = PULL_TO_REFRESH;
        isBeingDragged = false;

        if (null != headerLayout) {
            headerLayout.reset();
        }
        smoothScrollTo(0);
    }

    protected void setRefreshingInternal(boolean doScroll) {
        state = REFRESHING;

        if (null != headerLayout) {
            headerLayout.refreshing();
        }
        if (doScroll) {
            if (showViewWhileRefreshing) {
                smoothScrollTo(currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight : headerHeight);
            } else {
                smoothScrollTo(0);
            }
        }
    }



    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        //如果不允许刷新，直接返回false，事件向下传递
        if (!isPullToRefreshEnabled) {
            return false;

        }
        //如果正在刷新，事件自己处理
        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            //手指滑动时候的事件处理
            case MotionEvent.ACTION_MOVE: {
                //为pullMarginTop数组赋值，确定view距离window顶部的高度
                refreshableView.getLocationInWindow(pullMarginTop);
                if (isBeingDragged){
                    if (isSupportMultiPull) {
                        event.getActionIndex();
                        if (isMultiScrolling) {//多点触控
                            if (event.getPointerCount() >= 2) {
                                if (mode_multi == 1) {
                                    multiLastStartY = event.getY(0);
                                } else if (mode_multi == 2) {
                                    multiLastStartY = event.getY(1);

                                }
                            } else {
                                if (mode_multi == 1) {
                                    multiLastStartY = event.getY();
                                } else if (mode_multi == 2) {
                                    multiLastStartY = event.getY();
                                }
                            }

                            deltY = multiLastStartY - multiStartY;
                            lastMotionY = archY + deltY;
                        } else {
                            lastMotionY = event.getY();
                            archY = lastMotionY;
                        }
                    } else {
                        lastMotionY = event.getY();
                    }
                    this.pullEvent();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()) {
                    lastMotionY = initialMotionY = event.getY();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_1_DOWN:
                mode_multi = 1;
                archY = lastMotionY;
                if (isSupportMultiPull) {
                    multiStartY = event.getY(0);

                    if (isReadyForPull()) {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_1_UP:
                break;

            case MotionEvent.ACTION_POINTER_2_DOWN: {
                mode_multi = 2;
                archY = lastMotionY;
                isMultiScrolling = true;

                if (isSupportMultiPull) {
                    multiStartY = event.getY(1);

                    if (isReadyForPull()) {
                        return true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_2_UP:

                break;
            //松手之后的事件处理
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                isMultiScrolling = false;
                mode_multi = -1;
                if (isBeingDragged) {
                    isBeingDragged = false;
                    if (state == RELEASE_TO_REFRESH && null != onRefreshListener) {
                        if (isShowHeader){
                            switch (currentMode) {
                                case MODE_PULL_UP_TO_REFRESH:
                                    setHeaderScroll(headerHeight);
                                    break;
                                case MODE_PULL_DOWN_TO_REFRESH:
                                default:
                                    setHeaderScroll(-headerHeight);
                                    break;
                            }
                            setRefreshingInternal(true);
                        }else {
                            smoothScrollTo(0);
                        }

                        onRefreshListener.onRefresh();
                    } else if (state == RELEASE_TO_RECOVER&& isSupportedRedPacket){
                        state = PULL_TO_REFRESH;
                        if (redPacketRefreshListener!=null){
                            redPacketRefreshListener.onRefreshRedPacket();
                        }
                        smoothScrollTo(0);
                    }else {
                        smoothScrollTo(0);
                    }
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isPullToRefreshEnabled) {
            return false;
        }

        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isBeingDragged = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                mFlingUp = mLastTouchY - event.getY() > 0;
                if (isReadyForPull()) {
                    final float y = event.getY();
                    final float dy = y - lastMotionY;
                    final float yDiff = Math.abs(dy);
                    final float xDiff = Math.abs(event.getX() - lastMotionX);
                    if (yDiff > touchSlop && yDiff > xDiff) {
                        if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) && dy >= 0.0001f
                                && isReadyForPullDown()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH) {
                                currentMode = MODE_PULL_DOWN_TO_REFRESH;
                            }
                        } else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) && dy <= 0.0001f
                                && isReadyForPullUp()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH) {
                                currentMode = MODE_PULL_UP_TO_REFRESH;
                            }
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                mLastTouchY = event.getY();
                if (isReadyForPull()) {
                    lastMotionY = initialMotionY = event.getY();
                    lastMotionX = event.getX();
                    isBeingDragged = false;
                }
                refreshableView.getLocationInWindow(pullMarginTop);
                initalMarginTop = pullMarginTop[1];
                break;
            }
        }
        return isBeingDragged;
    }

    private boolean pullEvent() {

        int newHeight;
        final int oldHeight = this.getScrollY();
        if (headerLayout != null && isShowHeader) {
            headerLayout.setCurrentView(pullMarginTop[1], initalMarginTop);
        }
        switch (currentMode) {
            case MODE_PULL_UP_TO_REFRESH:
                newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0) / FRICTION);
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                if (isSupportMultiPull) {
                    newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0) / FRICTION);

                    if (null == mHeaderImageView || null == headerLayout)
                        break;

                    if (newHeight <= -mHeaderImageView.getMeasuredHeight() - headerLayout.getMeasuredHeight()) {
                        newHeight = -mHeaderImageView.getMeasuredHeight() - headerLayout.getMeasuredHeight();
                    }
                } else {
                    newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0) / FRICTION);
                }
                beginToPullDownForRefresh();
                break;
        }

        setHeaderScroll(newHeight);
        if (newHeight != 0) {
            if (isSupportedRedPacket &&recoverHeight< Math.abs(newHeight)){
                state = RELEASE_TO_RECOVER;

                if (headerLayout != null) {
                    headerLayout.pullRefreshRedPacket();
                }
                return true;
            }else {
                state = PULL_TO_REFRESH;
            }
            if (state == PULL_TO_REFRESH && headerHeight < Math.abs(newHeight)) {
                state = RELEASE_TO_REFRESH;
                switch (currentMode) {
                    case MODE_PULL_DOWN_TO_REFRESH:
                        if (headerLayout != null) {
                            headerLayout.releaseToRefresh();
                        }
                        break;
                }
                return true;
            } else if (headerHeight >= Math.abs(newHeight)) {
                if (state == RELEASE_TO_REFRESH){//当下拉超过headerHeight高度调用  会频繁调用  注意特殊处理
                    state = PULL_TO_REFRESH;
                    switch (currentMode) {
                        case MODE_PULL_DOWN_TO_REFRESH:
                            if (headerLayout != null) {
                                headerLayout.pullToRefresh();
                            }
                            break;
                    }
                }else {//只要下拉的高度小于headerHeight处理 会频繁调用  注意特殊处理
                    if (state == PULL_TO_REFRESH||state==RELEASE_TO_RECOVER){
                        if (headerLayout != null) {
                            headerLayout.pull();
                        }
                    }
                }
                return true;
            }
        }

        return oldHeight != newHeight;
    }

    public void beginToPullDownForRefresh(){

    }
    private boolean isReadyForPull() {
        switch (mode) {
            case MODE_PULL_DOWN_TO_REFRESH:
                return isReadyForPullDown();
            case MODE_PULL_UP_TO_REFRESH:
                return isReadyForPullUp();
            case MODE_BOTH:
                return isReadyForPullUp() || isReadyForPullDown();
        }
        return false;
    }


    protected final void setHeaderScroll(int y) {
        scrollTo(0, y);
    }

    protected final void smoothScrollTo(int y) {
        if (null != currentSmoothScrollRunnable) {
            currentSmoothScrollRunnable.stop();
        }

        if (this.getScrollY() != y) {
            this.currentSmoothScrollRunnable = new SmoothScrollRunnable(handler, getScrollY(), y);
            handler.post(currentSmoothScrollRunnable);
        }
    }


    protected final int getCurrentMode() {
        return currentMode;
    }

    protected final PullLoadingBaseLayout getHeaderLayout() {
        return headerLayout;
    }

    protected final int getHeaderHeight() {
        return headerHeight;
    }

    protected final int getMode() {
        return mode;
    }

    public final void setMode(int mode) {
        this.mode = mode;
        this.currentMode = mode;
    }

    protected final int getState() {
        return state;
    }

    protected abstract boolean isReadyForPullDown();

    protected abstract boolean isReadyForPullUp();

    @Override
    public void setLongClickable(boolean longClickable) {
        getRefreshableView().setLongClickable(longClickable);
    }

    public interface OnLastItemVisibleListener {
        void onLastItemVisible();
    }

    public interface
    OnRefreshListener {
        void onRefresh();
    }

    public interface OnRedPacketRefreshListener{
        void onRefreshRedPacket();
    }


    public void setRedPacketRefreshListener(OnRedPacketRefreshListener onRedPacketRefreshListener){
        this.redPacketRefreshListener = onRedPacketRefreshListener;
    }

    final class SmoothScrollRunnable implements Runnable {

        static final int ANIMATION_DURATION_MS = 400;
        static final int ANIMATION_FPS = 1000 / 60;

        private final Interpolator interpolator;
        private final int scrollToY;
        private final int scrollFromY;
        private final Handler handler;

        private boolean continueRunning = true;
        private long startTime = -1;
        private int currentY = -1;

        public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
            this.handler = handler;
            this.scrollFromY = fromY;
            this.scrollToY = toY;
            this.interpolator = new AccelerateDecelerateInterpolator();
        }

        @Override
        public void run() {

            /**
             * Only set startTime if this is the first time we're starting, else
             * actually calculate the Y delta
             */
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            } else {
                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - startTime)) / ANIMATION_DURATION_MS;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math.round((scrollFromY - scrollToY)
                        * interpolator.getInterpolation(normalizedTime / 1000f));
                this.currentY = scrollFromY - deltaY;
                setHeaderScroll(currentY);
            }

            // If we're not at the target Y, keep going...
            if (continueRunning && scrollToY != currentY) {
                handler.postDelayed(this, ANIMATION_FPS);
            }
        }

        public void stop() {
            this.continueRunning = false;
            this.handler.removeCallbacks(this);
        }
    }
}
