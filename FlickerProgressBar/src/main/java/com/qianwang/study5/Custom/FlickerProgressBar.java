package com.qianwang.study5.Custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.qianwang.study5.R;

/**
 * Created by luo on 2017/5/27.
 */

public class FlickerProgressBar extends View {

    private int textSize;
    private int loadingColor;
    private int stopColor;
    private int mWidth;
    private int mHeight;
    private int defHeight = 88;
    private Bitmap bgBitmap;
    private Paint mPaint;
    private static final int MAX_PROGRESS = 100;
    private int progress;
    private boolean isStop = false;
    private Bitmap flickerBitmap;//闪烁的图片
    private int flickerLeft;  //闪烁图片的开始位置
    private String progressText;
    private Rect bound;

    public FlickerProgressBar(Context context) {
        this(context, null);
    }

    public FlickerProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlickerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        bound=new Rect();

    }

    public void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.FlickerProgressBar);
        textSize = (int) array.getDimension(R.styleable.FlickerProgressBar_textSize, dp2px(14));
        loadingColor = array.getColor(R.styleable.FlickerProgressBar_loadingColor, Color.BLUE);
        stopColor = array.getColor(R.styleable.FlickerProgressBar_stopColor, Color.YELLOW);
    }

    private float dp2px(int dpValue) {

        float scale = getContext().getResources().getDisplayMetrics().density;
        return scale * dpValue + 0.5f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int siezWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        if (modeHeight == MeasureSpec.EXACTLY) {
            mHeight = sizeHeight;
        } else {
            mHeight = defHeight;
        }
        mWidth = siezWidth;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.画边界
        drawBorder(canvas);
        //2.画进度
        drawProgress(canvas);
        drawBitmap(bgBitmap);
        //3进度文字
        drawProgressText(canvas);

        //4画颜色渐变的进度文字
        drawColorProgressText(canvas);


    }

    private void drawColorProgressText(Canvas canvas) {


    }

    private void drawProgressText(Canvas canvas) {

        Paint textPaint=new Paint();
        progressText=getProgressText();
        textPaint.getTextBounds(progressText,0,progressText.length(),bound);
        int xWidth=(getMeasuredWidth()-bound.width())/2;
        int xHeight=(getMeasuredHeight()+bound.height())/2;
        canvas.drawText(progressText,xWidth,xHeight,textPaint);

    }

    private void drawBitmap(Bitmap dbBitmap) {


    }

    private void drawProgress(Canvas canvas) {

        mPaint.setColor(loadingColor);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        int right = (progress / MAX_PROGRESS) * getMeasuredWidth();
        bgBitmap = Bitmap.createBitmap(Math.max(right, 1), mHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bgBitmap);
        canvas.drawColor(loadingColor);
        if (!isStop) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(flickerBitmap, flickerLeft, 0, mPaint);
            mPaint.setXfermode(null);
        }
    }

    private void drawBorder(Canvas canvas) {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(loadingColor);
        mPaint.setStrokeWidth(6);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
    }

    public String getProgressText() {


        return null;
    }
}
