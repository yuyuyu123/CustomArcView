package com.yulin.customarcview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by YuLin on 2017/2/23 0023.
 * An custom widget called custom arc view for showing some texts in a arc.
 */
public class CustomArcView extends View {
    private static final String TAG = CustomArcView.class.getSimpleName();

    private static final float DEFAULT_STROKE = 15;
    private static final float DEFAULT_START_ANGLE = 150;
    private static final float DEFAULT_SWIPE_ANGLE = 240;
    private static final int DEFAULT_SLEEP_TIME = 5;

    private Paint mPaint;

    private int mWidth;
    private int mHeight;

    private int mCenterX;
    private int mCenterY;

    private RectF mRectOval;
    private int mArcInsideColor = Color.GRAY;
    private int mArcOutSideColor = Color.BLUE;

    private float mArcInsideStroke = DEFAULT_STROKE;
    private float mArcOutSideStroke = DEFAULT_STROKE + 5;
    private float mRadius;
    private float mStartAngle = DEFAULT_START_ANGLE;
    private float mSwipeAngle = DEFAULT_SWIPE_ANGLE;

    private int mSleepTime = DEFAULT_SLEEP_TIME;

    private int mProgress = 0;

    private double mRatio;

    private String mTxtTop;
    private float mTxtTopSize;
    private int mTxtTopColor = Color.BLACK;
    private Rect mTxtTopRect;

    private String mTxtCenter;
    private float mTxtCenterSize;
    private int mTxtCenterColor = Color.BLACK;
    private Rect mTxtCenterRect;

    private String mTxtBottom;
    private float mTxtBottomSize;
    private int mTxtBottomColor = Color.BLACK;
    private Rect mTxtBottomRect;

    public CustomArcView(Context context) {
        this(context,null);
    }

    public CustomArcView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.CustomArcView);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomArcView_arcInsideColor:
                    mArcInsideColor = a.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.CustomArcView_arcOutSideColor:
                    mArcOutSideColor = a.getColor(attr, Color.BLUE);
                    break;
                case R.styleable.CustomArcView_arcInsideStroke:
                    mArcInsideStroke = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomArcView_arcOutSideStroke:
                    mArcOutSideStroke = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomArcView_radius:
                    mRadius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomArcView_sleepTime:
                    mSleepTime = a.getInt(attr, DEFAULT_SLEEP_TIME);
                    break;
                case R.styleable.CustomArcView_ratio:
                    mRatio = a.getFloat(attr, 0);
                    break;
                case R.styleable.CustomArcView_txtTop:
                    mTxtTop = a.getString(attr);
                    break;
                case R.styleable.CustomArcView_txtTopColor:
                    mTxtTopColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomArcView_txtTopSize:
                    mTxtTopSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomArcView_txtCenter:
                    mTxtCenter = a.getString(attr);
                    break;
                case R.styleable.CustomArcView_txtCenterColor:
                    mTxtCenterColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomArcView_txtCenterSize:
                    mTxtCenterSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomArcView_txtBottom:
                    mTxtBottom = a.getString(attr);
                    break;
                case R.styleable.CustomArcView_txtBottomColor:
                    mTxtBottomColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomArcView_txtBottomSize:
                    mTxtBottomSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        setRatio(mRatio);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mWidth = w;
        mHeight = h;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        if(mRadius + mArcOutSideStroke > Math.min(w / 2, h / 2) || mRadius <= 0) {
            mRadius = Math.min(w / 2, h / 2) - mArcOutSideStroke;
        }
        mRectOval = new RectF(mCenterX - mRadius,mCenterY - mRadius,mCenterX + mRadius,mCenterY + mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawArc(canvas);

        mPaint.setStrokeWidth(0);

        drawTxtTop(canvas);

        drawTxtCenter(canvas);

        drawTxtBottom(canvas);
    }

    /**
     * Draw two arcs of background inside and progress outside.
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        mPaint.setStrokeWidth(mArcInsideStroke);
        mPaint.setColor(mArcInsideColor);
        canvas.drawArc(mRectOval,mStartAngle,mSwipeAngle,false,mPaint);

        mPaint.setStrokeWidth(mArcOutSideStroke);
        mPaint.setColor(mArcOutSideColor);
        if(BuildConfig.DEBUG) Log.d(TAG, "draw progress:" + mProgress);
        canvas.drawArc(mRectOval,mStartAngle,mProgress ==0 ? 0.01f : mProgress,false,mPaint);
    }

    /**
     * Draw text top.
     * @param canvas
     */
    private void drawTxtTop(Canvas canvas) {
        if(mTxtTopSize == 0) {
            mTxtTopSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getContext().getResources().getDisplayMetrics());
        }

        mPaint.setColor(mTxtTopColor);
        mPaint.setTextSize(mTxtTopSize);
        mTxtTopRect = new Rect();
        mPaint.getTextBounds(mTxtTop, 0, mTxtTop.length(), mTxtTopRect);
        canvas.drawText(mTxtTop, mCenterX - mTxtTopRect.width() / 2, mCenterY - Math.max(mCenterY / 3, mTxtTopRect.height()), mPaint);
    }

    /**
     * Draw text center.
     * @param canvas
     */
    private void drawTxtCenter(Canvas canvas) {
        if(mTxtCenterSize == 0) {
            mTxtCenterSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getContext().getResources().getDisplayMetrics());
        }

        mPaint.setColor(mTxtCenterColor);
        mPaint.setTextSize(mTxtCenterSize);
        mTxtCenterRect = new Rect();
        mPaint.getTextBounds(mTxtCenter, 0, mTxtCenter.length(), mTxtCenterRect);
        canvas.drawText(mTxtCenter, mCenterX - mTxtCenterRect.width() / 2, (float) (mCenterY), mPaint);
    }

    /**
     * Draw text bottom.
     * @param canvas
     */
    private void drawTxtBottom(Canvas canvas) {
        if(mTxtBottomSize == 0) {
            mTxtBottomSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getContext().getResources().getDisplayMetrics());
        }

        mPaint.setColor(mTxtBottomColor);
        mPaint.setTextSize(mTxtBottomSize);
        mTxtBottomRect = new Rect();
        mPaint.getTextBounds(mTxtBottom, 0, mTxtBottom.length(), mTxtBottomRect);
        canvas.drawText(mTxtBottom, mCenterX - mTxtBottomRect.width() / 2, (float) (mCenterY + Math.max(mCenterY / 3, mTxtBottomRect.height() * 2)), mPaint);
    }

    /**
     * Saved state for arc's progress.
     */
    private static class SavedState extends BaseSavedState {
        int progress;
        /**
         * Constructor called from {@link CustomArcView#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = mProgress;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setProgress(ss.progress);
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

    /**
     * Get sleep time which makes influenced on arc outside's drawing speed.
     * @return
     */
    public int getSleepTime() {
        return mSleepTime;
    }

    /**
     * Set sleep time which makes influence on the arc's drawing speed.
     * @param sleepTime
     */
    public void setSleepTime(int sleepTime) {
        this.mSleepTime = sleepTime;
    }

    /**
     * Get arc inside color.
     * @return
     */
    public int getArcInsideColor() {
        return mArcInsideColor;
    }

    /**
     * Set arc inside color.
     * @param arcInsideColor
     */
    public void setArcInsideColor(int arcInsideColor) {
        this.mArcInsideColor = arcInsideColor;
        invalidate();
    }

    /**
     * Get arc outside color.
     * @return
     */
    public int getArcOutSideColor() {
        return mArcOutSideColor;
    }

    /**
     * Set arc outside color.
     * @param arcOutSideColor
     */
    public void setArcOutSideColor(int arcOutSideColor) {
        this.mArcOutSideColor = arcOutSideColor;
        invalidate();
    }

    /**
     * Get arc inside stroke.
     * @return
     */
    public float getArcInsideStroke() {
        return mArcInsideStroke;
    }

    /**
     * Set arc inside stroke which makes influence on the arc's width.
     * @param arcInsideStroke
     */
    public void setArcInsideStroke(float arcInsideStroke) {
        this.mArcInsideStroke = arcInsideStroke;
        invalidate();
    }

    /**
     * Get arc outside's stroke.
     * @return
     */
    public float getArcOutSideStroke() {
        return mArcOutSideStroke;
    }

    /**
     * Set arc outside's stroke which makes influenced on arc's width.
     * @param arcOutSideStroke
     */
    public void setArcOutSideStroke(float arcOutSideStroke) {
        this.mArcOutSideStroke = arcOutSideStroke;
        invalidate();
    }

    /**
     * Get arc's radius.
     * @return
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * Set arc's radius which makes influenced on arc's width and height.
     * @param radius
     */
    public void setRadius(float radius) {
        this.mRadius = radius;
    }

    /**
     * Get ratio.
     * @return
     */
    public double getRatio() {
        return mRatio;
    }

    /**
     * Set ratio which makes influenced on the arc outside's swipe angle.
     * @param ratio
     */
    public void setRatio(double ratio) {
        this.mRatio = ratio;
        mProgress = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(mProgress < mSwipeAngle * mRatio) {
                        mProgress++;
                        postInvalidate();
                        try {
                            Thread.sleep(mSleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
        }).start();
    }

    /**
     * Get text top.
     * @return
     */
    public String getTxtTop() {
        return mTxtTop;
    }

    /**
     * Set text top.
     * @param txtTop
     */
    public void setTxtTop(String txtTop) {
        this.mTxtTop = txtTop;
        invalidate();
    }

    /**
     * Get text top's size.
     * @return
     */
    public float getTxtTopSize() {
        return mTxtTopSize;
    }

    /**
     * Set text top's size.
     * @param txtTopSize
     */
    public void setTxtTopSize(float txtTopSize) {
        this.mTxtTopSize = txtTopSize;
        invalidate();
    }

    /**
     * Get text top's color.
     * @return
     */
    public int getTxtTopColor() {
        return mTxtTopColor;
    }

    /**
     * Set text top's color.
     * @param txtTopColor
     */
    public void setTxtTopColor(int txtTopColor) {
        this.mTxtTopColor = txtTopColor;
        invalidate();
    }

    /**
     * Get text center.
     * @return
     */
    public String getTxtCenter() {
        return mTxtCenter;
    }

    /**
     * Set text center.
     * @param txtCenter
     */
    public void setTxtCenter(String txtCenter) {
        this.mTxtCenter = txtCenter;
        invalidate();
    }

    /**
     * Get text center's size.
     * @return
     */
    public float getTxtCenterSize() {
        return mTxtCenterSize;
    }

    /**
     * Set text center's size.
     * @param txtCenterSize
     */
    public void setTxtCenterSize(float txtCenterSize) {
        this.mTxtCenterSize = txtCenterSize;
        invalidate();
    }

    /**
     * Get text center's color.
     * @return
     */
    public int getTxtCenterColor() {
        return mTxtCenterColor;
    }

    /**
     * Set text center's color.
     * @param txtCenterColor
     */
    public void setTxtCenterColor(int txtCenterColor) {
        this.mTxtCenterColor = txtCenterColor;
        invalidate();
    }

    /**
     * Get text bottom.
     * @return
     */
    public String getTxtBottom() {
        return mTxtBottom;
    }

    /**
     * Set text bottom.
     * @param txtBottom
     */
    public void setTxtBottom(String txtBottom) {
        this.mTxtBottom = txtBottom;
        invalidate();
    }

    /**
     * Get text bottom's size.
     * @return
     */
    public float getTxtBottomSize() {
        return mTxtBottomSize;
    }

    /**
     * Set text bottom's size.
     * @param txtBottomSize
     */
    public void setTxtBottomSize(float txtBottomSize) {
        this.mTxtBottomSize = txtBottomSize;
        invalidate();
    }

    /**
     * Get text bottom's color.
     * @return
     */
    public int getTxtBottomColor() {
        return mTxtBottomColor;
    }

    /**
     * Set text bottom's color.
     * @param txtBottomColor
     */
    public void setTxtBottomColor(int txtBottomColor) {
        this.mTxtBottomColor = txtBottomColor;
        invalidate();
    }
}
