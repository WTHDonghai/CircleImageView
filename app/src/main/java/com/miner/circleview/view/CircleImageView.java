package com.miner.circleview.view;

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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.miner.circleview.R;

/**
 * Created by jgw on 2016-08-29.
 */
public class CircleImageView extends ImageView {

    //------图片缩放模式
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private ScaleType mScaleType = SCALE_TYPE;

    //------边框常量相关
    private static final int BORDER_COLOR_DEFAULT = 123;
    private static final int BORDER_WIDTH_DEFALUT = 2;
    private static final String TAG = "CircleImageView";

    //------图片常量相关
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    //------边框变量相关
    private float mBorderRadius;
    private int mBorderColor = BORDER_COLOR_DEFAULT;
    private int mBorderWidth = BORDER_WIDTH_DEFALUT;
    private RectF mBorderRect = new RectF();

    //------图片变量相关
    private float mBitmapRadius;
    private float mBitmapWidth;
    private float mBitmapHeight;
    private Matrix mBitmapMatrix = new Matrix();
    private RectF mBitmapRect = new RectF();
    private Bitmap mBitmap;

    //------边框的画笔相关
    private Paint mBorderPaint = new Paint();

    //-----边框的画笔相关
    private Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (null != attrs) {
            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);

            mBorderWidth = typeArray.getDimensionPixelOffset(R.styleable.CircleImageView_border_width, BORDER_WIDTH_DEFALUT);
            mBorderColor = typeArray.getColor(R.styleable.CircleImageView_border_color, BORDER_COLOR_DEFAULT);

            typeArray.recycle();
        }
        setup();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setup();
        invalidate();
        Log.d(TAG, "onLayout...");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setup();
        invalidate();

        Log.d(TAG, "onSizeChanged...");
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        initBitmap();
        setup();
        Log.d(TAG, "setImageDrawable...");
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);

        initBitmap();
        setup();
        Log.d(TAG, "setImageUri...");
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        initBitmap();
        setup();
        Log.d(TAG, "setImageDrawable...");
    }

    /**
     * 初始化位图对象
     */
    private void initBitmap() {

        mBitmap = createBitmapFromDrawable(getDrawable());
    }

    /**
     * 从资源文件中创建位图对象
     *
     * @param drawable
     * @return
     */
    private Bitmap createBitmapFromDrawable(Drawable drawable) {

        //考虑如果是颜色
        //考虑如果是图形
        //考虑如果是图片
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setup();

        Log.d(TAG, "setImageResource...");
    }

    private void setup() {

        if (getWidth() == 0 && getHeight() == 0) {
            invalidate();
            return;
        }

        if (null == mBitmap) {
            Log.d(TAG, "Bitmap is null..");
            return;
        }

        super.setScaleType(mScaleType);

        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);

        mBorderRect = calucateBounds();

        mBorderRadius = Math.min((mBorderRect.width() - mBorderWidth) / 2.0f, (mBorderRect.height() - mBorderWidth) / 2.0f);

        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();

        mBitmapPaint.setAntiAlias(true);
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapRect.set(mBorderRect);
        mBitmapPaint.setShader(mBitmapShader);

        mBitmapRadius = Math.min(mBitmapRect.width() / 2, mBitmapRect.height() / 2);

        updateShaderMatrix();
        Log.d(TAG, "边框的半径" + mBorderRadius);
        invalidate();
    }

    /**
     * 更新矩阵
     */
    private void updateShaderMatrix() {

        float scale = 0;//缩放比
        float dx = 0;//矩阵平移偏移量
        float dy = 0;

        mBitmapMatrix.set(null);

        /*
            w1:图片的宽
            h1:图片的高

            w:原点的宽
            h:原点的高

            w/w1 > h/h1,以h作为缩放比

            w/w1 < h/h1,以w作为缩放比

            画图可证
         */
        if (mBorderRect.width() * mBitmapHeight < mBorderRect.height() * mBitmapWidth) {

            scale = mBitmapRect.height() / mBitmapHeight;
            dx = (mBitmapRect.width() - mBitmapWidth * scale)*.5f;

            Log.d(TAG,"trans dx");
        } else {
            scale = mBitmapRect.width() / mBitmapWidth;
            dy = (mBitmapRect.height() - mBitmapHeight * scale)*.5f;
            Log.d(TAG,"trans dy");
        }

        mBitmapMatrix.setScale(scale, scale);
        mBitmapMatrix.postTranslate((int) (dx +.5f)+ mBitmapRect.left, (int) (dy+.5f) + mBitmapRect.top);
        mBitmapShader.setLocalMatrix(mBitmapMatrix);
    }

    /**
     * 裁剪矩形
     *
     * @return
     */
    private RectF calucateBounds() {

        float availWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float availHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        float sideLength = Math.min(availWidth, availHeight);

        float top = getHeight() - (availHeight + sideLength) / 2.0f;
        float left = getWidth() - (availWidth + sideLength) / 2.0f;

        RectF rect = new RectF(left, top, left + sideLength - getPaddingLeft() - getPaddingRight(), top + sideLength - getPaddingTop() - getPaddingBottom());

        return rect;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘画边框
//        canvas.drawRect(mBitmapRect,mBitmapPaint);
        canvas.drawCircle(mBitmapRect.centerX(), mBitmapRect.centerY(), mBitmapRadius, mBitmapPaint);
        canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
    }


    //-----------提供对外设置接口
    public void setBorderColor(int color) {
        this.mBorderColor = color;
        setup();
    }

    public void setBorderWidth(int width) {
        this.mBorderWidth = width;
        setup();
    }

}