package com.iyuba.voa.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.iyuba.voa.R;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/13
 * 邮箱：jxfengmtx@gmail.com
 * 取自bbc六分英语
 */
public class PlayProgressBarView extends View {
    // 底色圆环的画笔
    private Paint bgPaint;
    // 底色圆环的颜色
    private int bgColor;
    // 进度圆的画笔
    private Paint ringProgressPaint;
    // 进度圆颜色
    private int ringProgressColor;

    private float ringWidth;
    private int max;
    private int progress;

    public PlayProgressBarView(Context context) {
        this(context, null);
        initPaint();
    }

    public PlayProgressBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initAttrs(context, attrs);
        initPaint();
    }

    public PlayProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initPaint() {
        bgPaint = new Paint();
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(ringWidth);
        bgPaint.setAntiAlias(true);

        ringProgressPaint = new Paint();
        ringProgressPaint.setColor(ringProgressColor);
        ringProgressPaint.setStrokeWidth(ringWidth);
        ringProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        ringProgressPaint.setAntiAlias(true);
        ringProgressPaint.setStyle(Paint.Style.STROKE);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarView);
        bgColor = typedArray.getColor(R.styleable.ProgressBarView_ringColor, Color.GRAY);
        ringProgressColor = typedArray.getColor(R.styleable.ProgressBarView_ringProgressColor, Color.GREEN);
        ringWidth = typedArray.getDimension(R.styleable.ProgressBarView_ringWidth, 5);
        max = typedArray.getInteger(R.styleable.ProgressBarView_max, 100);
        //资源回收
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int xCenter = getWidth() / 2;
        int yCenter = getHeight() / 2;
        int radius = (int) (xCenter - ringWidth / 2);
        // 绘制背景圆
        canvas.drawCircle(xCenter, yCenter, radius, bgPaint);
        // 绘制进度圆
        RectF rectF = new RectF(xCenter - radius, yCenter - radius, xCenter + radius, yCenter + radius);
        canvas.drawArc(rectF, -90, progress * 360 / max, false, ringProgressPaint);

    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    public Paint getBgPaint() {
        return bgPaint;
    }

    public void setBgPaint(Paint bgPaint) {
        this.bgPaint = bgPaint;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public Paint getRingProgressPaint() {
        return ringProgressPaint;
    }

    public void setRingProgressPaint(Paint ringProgressPaint) {
        this.ringProgressPaint = ringProgressPaint;
    }

    public int getRingProgressColor() {
        return ringProgressColor;
    }

    public void setRingProgressColor(int ringProgressColor) {
        this.ringProgressColor = ringProgressColor;
    }

    public float getRingWidth() {
        return ringWidth;
    }

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }
}