package com.qianwang.study5.Custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.qianwang.study5.R;

/**
 * Created by luo on 2017/5/27.
 */

public class FlickerProgressBar extends View implements Runnable {

    private int textSize;
    private int loadingColor;
    private int stopColor;
    private int mWidth;
    private int mHeight;
    private int defHeight = 88;
    private Bitmap pgBitmap;
    private Paint mPaint;
    private static final int MAX_PROGRESS = 100;
    private float progress;
    private boolean isStop = true;
    private Bitmap flickerBitmap;//闪烁的图片
    private int flickerLeft;  //闪烁图片移动到最左的位置
    private String progressText;
    private Rect bound;
    private Paint textPaint;
    private boolean isFinish = false;
    private int progressColor = loadingColor;
    private Thread thread;
    private BitmapShader bitmapShader;
    private Canvas pgCanvas;

    public FlickerProgressBar(Context context) {
        this(context, null);
    }

    public FlickerProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlickerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        bound = new Rect();

    }

    public void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.FlickerProgressBar);
        textSize = (int) array.getDimension(R.styleable.FlickerProgressBar_textSize, dp2px(14));
        loadingColor = array.getColor(R.styleable.FlickerProgressBar_loadingColor, Color.BLUE);
        stopColor = array.getColor(R.styleable.FlickerProgressBar_stopColor, Color.YELLOW);
    }


    private void init() {
        flickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flicker);
        flickerLeft = -flickerBitmap.getWidth();

        pgBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        pgCanvas = new Canvas(pgBitmap);
        thread = new Thread(this);
        thread.start();


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

        if (pgBitmap == null) {
            init();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.画边界
        drawBorder(canvas);
        //2.画进度
        drawProgress(canvas);
        //3进度文字
        drawProgressText(canvas);
        //4画颜色渐变的进度文字
        drawColorProgressText(canvas);


    }

    /**
     * 歌词渐变也可以这样处理。或者用shader（渲染器）
     *
     * @param canvas
     */
    private void drawColorProgressText(Canvas canvas) {

        textPaint.setColor(Color.WHITE);

        int xWidth = (getMeasuredWidth() - bound.width()) / 2;
        int xHeight = (getMeasuredHeight() + bound.height()) / 2;
        float progressWidth = (progress / MAX_PROGRESS) * getMeasuredWidth();
        if (progressWidth > xWidth) {
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            float right = Math.min(progressWidth, xWidth + bound.width());
            canvas.clipRect(xWidth, 0, right, getMeasuredHeight());
            canvas.drawText(progressText, xWidth, xHeight, textPaint);
            canvas.restore();
        }
    }

    private void drawProgressText(Canvas canvas) {

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        progressText = getProgressText();
        textPaint.getTextBounds(progressText, 0, progressText.length(), bound);
        int xWidth = (getMeasuredWidth() - bound.width()) / 2;    //进度文本开始的位置
        int xHeight = (getMeasuredHeight() + bound.height()) / 2;  //进度文本结束的位置
        canvas.drawText(progressText, xWidth, xHeight, textPaint);

    }

    private void drawProgress(Canvas canvas) {

        mPaint.setColor(progressColor);
        mPaint.setStrokeWidth(6);
        mPaint.setStyle(Paint.Style.FILL);
        float right = (progress / MAX_PROGRESS) * getMeasuredWidth();
        pgCanvas.save(Canvas.CLIP_SAVE_FLAG);
        pgCanvas.clipRect(0, 0, right, getMeasuredHeight());
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();

        if (!isStop) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            pgCanvas.drawBitmap(flickerBitmap, flickerLeft, 0, mPaint);
            mPaint.setXfermode(null);
        }

        //控制显示区域
        bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(bitmapShader);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
    }

    private void drawBorder(Canvas canvas) {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(progressColor);
        mPaint.setStrokeWidth(6);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
    }

    public String getProgressText() {

        if (!isFinish) {
            if (!isStop) {
                progressText = "下载中" + progress + "%";
            } else {
                if (progress == 0) {
                    progressText = "下载";
                } else {
                    progressText = "继续";
                }
            }
        } else {
            progressText = "下载完成";
        }
        return progressText;
    }

    public void toggle() {

        if (!isFinish) {

            if (isStop) {
                setStop(false);
            } else {
                setStop(true);
            }
        }
    }

    public void setProgress(float progress) {

        if (!isStop) {
            if (progress < MAX_PROGRESS) {
                this.progress = progress;
            } else {
                this.progress = MAX_PROGRESS;
                isFinish = true;
            }
        }

    }

    public float getProgress() {

        return progress;
    }

    public boolean isStop() {

        return isStop;
    }

    public boolean isFinish() {

        return isFinish;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (isStop) {
            progressColor = stopColor;
            thread.interrupt();
        } else {
            progressColor = loadingColor;
            thread = new Thread(this);
            thread.start();
        }
        invalidate();
    }

    public void reset() {

        setStop(true);
        progress = 0;
        isFinish = false;
        isStop = false;
        progressColor = loadingColor;
        progressText = "下载";
        flickerLeft = -flickerBitmap.getWidth();
        init();


    }

    public void finishLoad() {
        isFinish = true;
        setStop(true);
    }

    @Override
    public void run() {
        int width = flickerBitmap.getWidth();
        try {
            while (!isStop && !thread.isInterrupted()) {
                flickerLeft += dp2px(5);
                float progressWidth =(progress / MAX_PROGRESS) * getMeasuredWidth();
                if (flickerLeft>= progressWidth) {
                    flickerLeft = -width;
                }
                postInvalidate();
                Thread.sleep(20);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
