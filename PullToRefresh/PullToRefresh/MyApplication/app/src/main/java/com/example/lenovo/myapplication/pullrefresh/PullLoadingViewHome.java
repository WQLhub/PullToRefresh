package com.example.lenovo.myapplication.pullrefresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.tuan800.tao800.R;

/**
 * Created by qjb on 2015/12/21.
 */
public class PullLoadingViewHome extends View {
    private static final int MAX_PIC_NUM = 42;
    private Context mContext;
    private Bitmap initialBitmap;
    private Bitmap endBitmap;
    private float switchPicDivider = 4.5f;//1080默认的除数大小

    public PullLoadingViewHome(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PullLoadingViewHome(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PullLoadingViewHome(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        initialBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pull_loading_view_0));
        endBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pull_loading_view_20));
        switchPicDivider = getResources().getDimension(R.dimen.pull_to_refresh_hegiht) / (MAX_PIC_NUM + 1);
    }

    public void setCurrentView(int viewMaiginTop, int initialMarginTop) {
//        int topHeight = (int) (mContext.getResources().getDimensionPixelOffset(R.dimen.float_view_margin_top) + Tao800Util.getStatusHeight());
        int i = (int) ((viewMaiginTop - initialMarginTop) / switchPicDivider);
        if (i >= MAX_PIC_NUM) i = MAX_PIC_NUM;
        int imgId = getResources().getIdentifier("pull_loading_view_" + i, "drawable", mContext.getPackageName());
        initialBitmap = BitmapFactory.decodeResource(getResources(), imgId);
        setBackgroundDrawable(new BitmapDrawable(initialBitmap));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //根据设置的宽度来计算高度  设置为符合第二阶段娃娃图片的宽高比例
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(widthMeasureSpec) * endBitmap.getHeight() / endBitmap.getWidth());
    }

    private int measureWidth(int widMeasureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(widMeasureSpec);
        int mode = MeasureSpec.getMode(widMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = endBitmap.getWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

   /* @Override
    protected void onDraw(Canvas canvas) {
        LogUtil.d("qjb on onDraw");
        super.onDraw(canvas);
        if (initialBitmap!=null){
            canvas.drawBitmap(initialBitmap, 0, 0, null);
            LogUtil.d("qjb on initialBitmap.toString:"+initialBitmap.toString());
        }
    }*/
}
