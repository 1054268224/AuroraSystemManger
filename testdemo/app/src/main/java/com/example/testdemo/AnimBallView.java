package com.example.testdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimBallView extends View {

    public static final int LOCATION_LEFT_TOP = 0;
    public static final int LOCATION_RIGHT_TOP = 1;
    public static final int LOCATION_LEFT_BOTTOM = 2;
    public static final int LOCATION_RIGHT_BOTTOM = 3;

    //圆球的最大数量
    public static final int BALL_COUNT = 4;

    //结束坐标
    private int mEndX, mEndY;
    private Paint mPaint;
    //存放圆球的集合
    private List<BallBean> mBalls = new ArrayList<BallBean>();
    private WeakHandler mHandler;
    //动画是否开始
    private Boolean isAnimStart = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimBallView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimBallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimBallView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mHandler = new WeakHandler((Activity) context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获取中心点的位置
        mEndX = getMeasuredWidth() / 2;
        mEndY = getMeasuredHeight() / 2;

        //清除存放圆球的集合
        mBalls.clear();

        //创建圆球
        for (int i = 0; i < BALL_COUNT; i++) {
            BallBean mBall = new BallBean(i, getMeasuredWidth(), getMeasuredHeight(), mEndX, mEndY);
            mBalls.add(mBall);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制每个圆的位置
        onDrawBall(canvas);

        if(isAnimStart) {
            //开启延时刷新
            mHandler.sendEmptyMessageDelayed(1, 10);
        }
    }

    private void onDrawBall(Canvas canvas){
        for (int i = 0; i < mBalls.size(); i++) {
            BallBean mTempBall = mBalls.get(i);
            mPaint.setAlpha(mTempBall.mAlpha);
            canvas.drawCircle(mTempBall.mX, mTempBall.mY, mTempBall.mRadius,mPaint);
        }
    }

    public void onStartAnim() {
        isAnimStart = true;
        //开启延时刷新
        mHandler.sendEmptyMessageDelayed(1, 10);
    }

    public void onStopAnim(){
        mHandler.removeCallbacksAndMessages(null);
        isAnimStart = false;
    }

    //计算位置和大小
    private void onCalculationLocalWithSize(){
        for (int i = 0; i < mBalls.size(); i++) {
            BallBean mTempBall = mBalls.get(i);
            //计算当前球与终点的差距
            //到达终点，设置新的坐标
            mTempBall.onCalculationLocalWithSize(i);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        onStopAnim();
        super.onDetachedFromWindow();
    }

    class WeakHandler extends Handler {
        WeakReference<Activity> mActivity;
        public WeakHandler(Activity activity){
            mActivity = new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            if(mActivity.get() != null){
                switch (msg.what) {
                    case 1:
                        //计算移动后的位置
                        onCalculationLocalWithSize();

                        //重新draw
                        invalidate();
                        break;
                }
            }
        }
    }



    class BallBean{

        int mX;
        int mY;
        //球半径
        int mRadius;
        int mAlpha;

        private int mEndX;
        private int mEndY;
        private int mWidth;
        private int mHeight;
        private int mSpeedX;
        private int mSpeedY;
        private int mSpeedRadiu;
        private int mSpeedAlpah;
        //每次移动的距离（速度）
        private static final int BALL_SPEED = 20;

        public BallBean(int location, int width, int height, int endX, int endY) {
            this.mEndX = endX;
            this.mEndY = endY;
            this.mWidth = width;
            this.mHeight = height;

            init(location);
        }

        private void init(int location){

            Random mRandom = new Random();

            //只是生成随机数，无视这段话
            int xDiff = 100-mRandom.nextInt(200);
            int yDiff = 200-mRandom.nextInt(400);

            switch (location)
            {
                case LOCATION_LEFT_TOP://左上
                    mX = xDiff;
                    mY = yDiff;
                    break;
                case LOCATION_RIGHT_TOP://右上
                    mX = mWidth + xDiff;
                    mY = yDiff;
                    break;
                case LOCATION_LEFT_BOTTOM://左下
                    mX = xDiff;
                    mY = mHeight;
                    break;
                case LOCATION_RIGHT_BOTTOM://右下
                    mX = mWidth;
                    mY = mHeight + yDiff;
                    break;
            }

            int diffX = Math.abs(mEndX - mX );
            int diffY = Math.abs(mEndY - mY);

            //计算初始位置到终点的距离
            int diffZ = (int) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY,2));
            int mSpeedZ = diffZ / BALL_SPEED;
            int mUrplus = diffZ % BALL_SPEED;

            if(mUrplus > 0){
                mSpeedZ++;
            }

            if(mSpeedZ > 0) {
                mSpeedX = diffX / mSpeedZ;
                mSpeedY = diffY / mSpeedZ;
                mRadius = 100 - mRandom.nextInt(40);
                mSpeedRadiu = mRadius / mSpeedZ;
                mAlpha = 255;
                mSpeedAlpah = mAlpha / mSpeedZ;
            }
        }

        public void onCalculationLocalWithSize(int location){
            Boolean isChange = false;
            //计算新的位置
            if(Math.abs(mEndX - mX ) > mSpeedX){
                //如果从上往下移动
                if(mEndX > mX){
                    mX += mSpeedX;
                }else{
                    mX -= mSpeedX;
                }
                isChange = true;
            }else{
                mX = mEndX;
            }

            if(Math.abs(mEndY - mY) > mSpeedY){
                //如果从左往右移动
                if(mEndY > mY){
                    mY += mSpeedY;
                }else{
                    mY -= mSpeedY;
                }
                isChange = true;
            }else{
                mY = mEndY;
            }

            //缩小
            mRadius -= mSpeedRadiu;

            mAlpha -= mSpeedAlpah;
            if(mAlpha<0)
                mAlpha=0;

            //如果位置没有变，重置
            if(!isChange){
                //重置
                init(location);
            }
        }

    }
}
