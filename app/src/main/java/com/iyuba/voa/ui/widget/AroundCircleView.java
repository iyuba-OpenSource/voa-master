package com.iyuba.voa.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.elvishew.xlog.XLog;
import com.iyuba.voa.R;


/**
 * 类似于 音乐播放器的圆形图片 周边的进度条
 * 只需要设置图片  然后设置进度就行
 * 圆角显示图片  来自CircleImageView
 * 在基础上修改
 * <p>
 * 创建日期：2022/04/22
 */
public class AroundCircleView extends AppCompatImageView {

    private static final AppCompatImageView.ScaleType SCALE_TYPE = AppCompatImageView.ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.GREEN;
    private static final int DEFAULT_BORDER_COLOR_BG = Color.WHITE;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private final Paint mBorderPaint_bg = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderColor_bg = DEFAULT_BORDER_COLOR_BG;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mBorderRadius;
    private float mDrawableRadius;

    private boolean mReady;


    private boolean mSetupPending;
    private long newAngle;
    private long totalProcess;


    public AroundCircleView(Context context) {
        super(context);
    }

    public AroundCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AroundCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(SCALE_TYPE);


        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AroundCircleView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(
                R.styleable.AroundCircleView_textSize, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.AroundCircleView_textColor,
                DEFAULT_BORDER_COLOR);

        mBorderColor_bg = a.getColor(R.styleable.AroundCircleView_textBgColor,
                DEFAULT_BORDER_COLOR_BG);

        a.recycle();

        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public AppCompatImageView.ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(AppCompatImageView.ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format(
                    "ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius,
                mBitmapPaint); // 里面的图片


        if (mBorderWidth != 0) {
            //周边底部颜色 一般为白色
            canvas.drawArc(mBorderRect, -90, 360, false, mBorderPaint_bg);


            //设置了周边弧度的宽度 每次重新绘制都要画上边上的弧度
            canvas.drawArc(mBorderRect, -90, newAngle, false, mBorderPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }


    public void setTotalProcess(long totalProcess) {
        this.totalProcess = totalProcess;
    }

    public void setProgress(long progress) {
        /**
         *
         *0-100
         * 0-360
         */
        if (totalProcess == 0) {
            XLog.i("总进度为0");
            totalProcess = 1;
        }
//        long changeProgress = (long) (progress * 3.6);
        long changeProgress = (long) (progress * 1.0 / totalProcess * 100 * 3.6);
        XLog.i("进度：" + changeProgress);
        this.newAngle = changeProgress;
        postInvalidate();


    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                // 为0就自己加上需要的 要改不 就 传值 变化 或者 这里可以先测量一下？
                if (drawable.getIntrinsicWidth() <= 0) {
                    int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    measure(w, h);
                    int height = getMeasuredHeight();
                    int width = getMeasuredWidth();

                    System.out.println(height + "---" + width);

                    bitmap = Bitmap
                            .createBitmap(width, height, BITMAP_CONFIG);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(), BITMAP_CONFIG);
                }
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBorderPaint_bg.setStyle(Paint.Style.STROKE);
        mBorderPaint_bg.setAntiAlias(true);
        mBorderPaint_bg.setColor(mBorderColor_bg);
        mBorderPaint_bg.setStrokeWidth(mBorderWidth);


        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(mBorderWidth, mBorderWidth, getWidth() - mBorderWidth, getHeight() - mBorderWidth);

//         mDrawableRect.set(mBorderWidth/2, mBorderWidth/2, mBorderRect.width()
//                 - mBorderWidth/2, mBorderRect.height() - mBorderWidth/2);

        mDrawableRect.set(mBorderWidth / 2, mBorderWidth / 2, mBorderRect.width()
                + mBorderWidth / 2, mBorderRect.height() + mBorderWidth / 2);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2,
                mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
                * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
                (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}
