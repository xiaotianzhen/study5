package com.qianwang.weatherview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.qianwang.weatherview.HourItem;
import com.qianwang.weatherview.R;
import com.qianwang.weatherview.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luo on 2017/5/29.
 */

public class Today24HourView extends View {

    private int mWidth;
    private int mHeight;
    private int tempBaseTop;
    private int tempBaseBottom;
    private static final int MARGIN_LEFT_ITEM = 10;
    private static final int MARGIN_RIGHT_ITEM = 10;
    private static final int ITEM_SIZE = 24;
    private static final int ITEM_WIDTH = 100;
    private Paint pointPaint, linePaint, dashLinePaint, windyBoxPaint, textPaint;
    private static final int windyBoxAlpha = 80;
    private static final int windyBoxMaxHeight = 80;
    private static final int windyBoxMinHeight = 20;
    private static final int windyBoxSubHight = windyBoxMaxHeight - windyBoxMinHeight;
    private static final int bottomTextHeight = 60;
    private List<HourItem> mList = new ArrayList<HourItem>();


    private int maxTemp = 26;
    private int minTemp = 21;
    private int maxWindy = 5;
    private int minWindy = 2;
    private static final int TEMP[] = {22, 23, 23, 23, 23,
            22, 23, 23, 23, 22,
            21, 21, 22, 22, 23,
            23, 24, 24, 25, 25,
            25, 26, 25, 24};
    private static final int WINDY[] = {2, 2, 3, 3, 3,
            4, 4, 4, 3, 3,
            3, 4, 4, 4, 4,
            2, 2, 2, 3, 3,
            3, 5, 5, 5};
    private static final int WEATHER_RES[] = {R.mipmap.w0, R.mipmap.w1, R.mipmap.w3, -1, -1
            , R.mipmap.w5, R.mipmap.w7, R.mipmap.w9, -1, -1
            , -1, R.mipmap.w10, R.mipmap.w15, -1, -1
            , -1, -1, -1, -1, -1
            , R.mipmap.w18, -1, -1, R.mipmap.w19};

    public Today24HourView(Context context) {
        this(context, null);
    }

    public Today24HourView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Today24HourView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        mWidth = MARGIN_LEFT_ITEM + MARGIN_RIGHT_ITEM + ITEM_SIZE * ITEM_WIDTH;
        mHeight = 500;
        tempBaseTop = (500 - bottomTextHeight) / 4;
        tempBaseBottom = (500 - bottomTextHeight) * 2 / 3;
        initHourItems();
        intiPaint();

    }

    private void intiPaint() {
        pointPaint = new Paint();
        pointPaint.setColor(Color.WHITE);
        pointPaint.setAntiAlias(true);
        pointPaint.setTextSize(8);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);

        dashLinePaint = new Paint();
        dashLinePaint.setColor(Color.WHITE);
        PathEffect effect = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        dashLinePaint.setPathEffect(effect);
        dashLinePaint.setStrokeWidth(3);
        dashLinePaint.setAntiAlias(true);
        dashLinePaint.setStyle(Paint.Style.STROKE);

        windyBoxPaint = new Paint();
        windyBoxPaint.setTextSize(1);
        ;
        windyBoxPaint.setColor(Color.WHITE);
        windyBoxPaint.setAlpha(windyBoxAlpha);
        windyBoxPaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), 12));
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);

    }

    private void initHourItems() {

        for (int i = 0; i <= ITEM_SIZE; i++) {
            String time = "";
            if (i < 10) {
                time = "0" + i + ":00";
            } else {
                time = i + ":00";
            }

            int left = MARGIN_LEFT_ITEM + i * ITEM_WIDTH;
            int right = left + ITEM_WIDTH - 1;
            int top = (int) (mHeight - bottomTextHeight + (maxWindy - WINDY[i]) * 1.0 / (maxWindy - minWindy) *
                    windyBoxSubHight - windyBoxMaxHeight);
            int bottom = mHeight - bottomTextHeight;
            Rect rect = new Rect(left, top, right, bottom);
            Point point = calculateTempPoint(left, right, TEMP[i]);

            HourItem hourItem = new HourItem();
            hourItem.windyBoxRect = rect;
            hourItem.time = time;
            hourItem.windy = WINDY[i];
            hourItem.temperature = TEMP[i];
            hourItem.tempPoint = point;
            hourItem.res = WEATHER_RES[i];
            mList.add(hourItem);
        }
    }

    private Point calculateTempPoint(int left, int right, int i) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i=0;i<mList.size();i++){

            Rect rect=mList.get(i).windyBoxRect;
            Point point=mList.get(i).tempPoint;
            //画风力的box和提示文字
            onDrawBox(canvas,rect,i);
            //画温度的点
            onDrawTemp(canvas,i);
            //画表示天气的图片
            if(mList.get(i).res!=-1&&i!=currentItemIndex){

                Drawable drawable= ContextCompat.getDrawable(getContext(),mList.get(i).res);
                drawable.setBounds(point.x-DisplayUtil.dip2px(getContext(),10),point.y-DisplayUtil.dip2px(getContext(),25),
                point.x+DisplayUtil.dip2px(getContext(),10),point.y-DisplayUtil.dip2px(getContext(),5)
                        drawable.draw(canvas));
            }
            onDrawLine(canvas,i);
            onDrawText(canvas,i);
        }
        //底部水平的白线
        linePaint.setColor(Color.WHITE);
        canvas.drawLine(0,mHeight-bottomTextHeight,mWidth,mHeight-bottomTextHeight,linePaint);
    }



}
