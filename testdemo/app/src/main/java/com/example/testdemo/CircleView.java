package com.example.testdemo;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.telephony.CbGeoUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class CircleView extends View{
    public static final float RADIUS=50f;
    private CircleBall currentBall;
    private Paint paint=new Paint();
    private List<CircleBall> mCircleBalls = new ArrayList<CircleBall>();
    public CircleView(Context context) {
        super(context);
    }
    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    //确定控件的位置和颜色
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            if (currentBall == null) {
                starAnim();

            } else {
                paint.setColor(currentBall.getColor());
                canvas.drawCircle(currentBall.getX(), currentBall.getY(), currentBall.getRadius(), paint);
            }
    }
    //开始设计动画的效果
    private void starAnim() {
        CircleBall c1 = new CircleBall(new Random().nextInt(600),new Random().nextInt(300),RADIUS,Color.argb(255,149,200,252));
        CircleBall endCircle=new CircleBall(200,200,10f,Color.BLUE);
        ValueAnimator value=ValueAnimator.ofObject(new CircleEvalator(),c1,endCircle);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentBall = (CircleBall) animation.getAnimatedValue();
                invalidate();
            }
        });
        value.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("wtk", "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("wtk", "onAnimationEnd");
                currentBall = null;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("wtk", "onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d("wtk", "onAnimationRepeat");
            }
        });
        AnimatorSet set=new AnimatorSet();
        set.play(value);
        set.setDuration(3000);
        set.start();
    }
    //自己定义的模式里面计算开始到结束，原点和颜色的差值
    class CircleEvalator implements TypeEvaluator{
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            CircleBall startpoin=(CircleBall)startValue;
            CircleBall endpoin=(CircleBall)endValue;
            CircleBall startColor=(CircleBall)startValue;
            CircleBall endColor=(CircleBall)endValue;

            float x = startpoin.getX() + fraction * (endpoin.getX() - startpoin.getX());
            float y = startpoin.getY() + fraction * (endpoin.getY() - startpoin.getY());
            float r = startpoin.getRadius() + fraction * (endpoin.getRadius() - startpoin.getRadius());
            int alpha=(int)(Color.alpha(startColor.getColor())+fraction*(Color.alpha(endColor.getColor())-Color.alpha(startColor.getColor())));
            int red=(int)(Color.red(startColor.getColor())+fraction*(Color.red(endColor.getColor())-Color.red(startColor.getColor())));
            int green=(int)(Color.green(startColor.getColor())+fraction*(Color.green(endColor.getColor())-Color.green(startColor.getColor())));
            int blue=(int)(Color.blue(startColor.getColor())+fraction*(Color.blue(endColor.getColor())-Color.blue(startColor.getColor())));

            return new CircleBall(x,y,r,Color.argb(alpha,red,green,blue));
        }
    }

    public class CircleBall{
        public int NUM=0;
        private float x;
        private float y;
        private float r;
        private int color;

        public CircleBall(float x,float y,float r,int color){
            this.x=x;
            this.y=y;
            this.r=r;
            this.color=color;
        }

        @Override
        public String toString() {
            return "circle{" +
                    "x=" + x +
                    ", y=" + y +
                    ", r=" + r +
                    ", color=" + color +
                    '}';
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getRadius() {
            return r;
        }

        public void setRadius(float r) {
            this.r = r;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
