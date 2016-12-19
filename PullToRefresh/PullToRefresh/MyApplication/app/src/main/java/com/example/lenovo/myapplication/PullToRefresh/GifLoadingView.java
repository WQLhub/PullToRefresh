package com.example.lenovo.myapplication.PullToRefresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.lenovo.myapplication.R;

/**
 * Created by qiao on 2016/12/19
 *
 * 下拉过程是根据不同高度去替换图片，refreshing是gif图loading
 *
 */

public class GifLoadingView extends View {

    private static final int MAX_PIC_NUM = 4;
    private Context mContext;
    private Bitmap bitmap;
    private float switchDivider = 5f;
    private int bitmapWidth;
    private int bitmapHeight;


    public GifLoadingView(Context context) {
        super(context);
        init(context);
    }

    public GifLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /*
    * 1、构造函数和初始化方法
    * 2、onMeasure进行测量
    * 3、通过onSizeChanged获取View的最新宽高
    * 4、onDraw进行绘制
    * 5、对外提供接口和方法
    * */

    private void init(Context context){
        mContext = context;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.gif1);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
    }

    public void setCurrentView(int viewMarginTop,int initialMarginTop){
        int i = (int) ((viewMarginTop-initialMarginTop)/switchDivider);
        i = i>MAX_PIC_NUM?MAX_PIC_NUM:i;
        int imgId = getResources().getIdentifier("gif"+i,"drawable",mContext.getPackageName());
        bitmap = BitmapFactory.decodeResource(getResources(),imgId);
        setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec),measure(heightMeasureSpec));
    }

    private int measure(int measureSpec){
        int result = 0;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode==MeasureSpec.EXACTLY){
            result = size;
        }else {
            result = bitmapWidth;
            if (mode==MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }
        }
        return result;
    }

}
