package com.worain.xproject;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.face.IFaceService;
import android.os.SystemPropertiesProto;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class PowerAnimView extends View {

    private static final long CLICKTIME = 500;
    private ValueAnimator animator;
    private ValueAnimator autoAnimators;
    private boolean isclicked;

    public boolean isSvgstrategy() {
        return svgstrategy;
    }

    public void setSvgstrategy(boolean svgstrategy) {
        this.svgstrategy = svgstrategy;
    }

    public PowerAnimView(Context context) {
        super(context);
        init(context);
    }

    public PowerAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PowerAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public Drawable getSvg() {
        return svg;
    }

    public void setSvg(Drawable svg) {
        this.svg = svg;
    }

    private long presstime = 0;
    private boolean isdian = false; // 是否开始转圈
    boolean isdoaction;//// 是否已经触发action
    public long CHANGETIME = 200;// 什么时候开始转圈
    public float dianRadiuOrgin = 2; //点半径。
    public int DIANSHU = 24; // 多少个点
    public int TIMETIME = 4000;  // 按压多久触发动作
    public int JD = 5;  // 时间精度，
    private int dianRadiu; //2dp 点的半径
    private Paint mpaint;//白点
    private Paint mpaint2; //黑点
    private Paint mpaintbg;
    int bg = -1;  //背景色
    Drawable svg; // 中间图片
    boolean svgstrategy;
    private Drawable diansvg;

    public Drawable getDiansvg() {
        return diansvg;
    }

    public void setDiansvg(Drawable diansvg) {
        this.diansvg = diansvg;
    }

    private void init(Context context) {
        mpaint = new Paint();
        mpaint2 = new Paint();
        mpaintbg = new Paint();
        dianRadiu = (int) (dianRadiuOrgin * getResources().getDisplayMetrics().density);
        mpaint.setStrokeWidth(dianRadiu);
        mpaint2.setStrokeWidth(dianRadiu);
        mpaintbg.setAntiAlias(true);
        mpaint.setAntiAlias(true);
        mpaint2.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!svgstrategy)
            drawBG(canvas);
        drawSVG(canvas, svg);
        drawDian(canvas);
    }

    private void drawDian(Canvas canvas) {
        if (isdian) {
//            // 根据按压时间 计算需要显示的点的个数。
            int diancount = (int) (DIANSHU * (presstime - CHANGETIME) / (TIMETIME - CHANGETIME));
            if (diancount < 0) return;
            if (diancount > DIANSHU) diancount = DIANSHU;
//            canvas

            float rx = Math.min(canvas.getWidth() / 2, canvas.getWidth() / 2);
            rx = rx - dianRadiu;
            int x = canvas.getWidth() / 2;
            int y = canvas.getHeight() / 2;
            canvas.save();
            canvas.translate(x, y);
            mpaint.setColor(bg);
            for (int i = 0; i < diancount; i++) {
                drawDianSingle(i, canvas, mpaint, rx, true);
            }
            mpaint2.setColor(getResources().getColor(R.color.color_gray, null));
            for (int i = diancount; i < DIANSHU; i++) {
                drawDianSingle(i, canvas, mpaint2, rx, false);
            }
            canvas.restore();
        }
    }

    private void drawDianSingle(int i, Canvas canvas, Paint mpaint2, float rx, boolean issvg) {

        float divisionPercent = 360.0f / DIANSHU;
        float secondPercent = divisionPercent * i;
        float AndroidSecondAngel = secondPercent - 90;
        double SecondRadians = Math.toRadians(AndroidSecondAngel);
        float stopSecondX = (float) ((rx) * Math.cos(SecondRadians));
        float stopSecondY = (float) ((rx) * Math.sin(SecondRadians));
        if (diansvg != null && issvg) {
            stopSecondY = stopSecondY - 1;
            int right = diansvg.getIntrinsicWidth();
            int left = (int) (stopSecondX - diansvg.getIntrinsicWidth() / 2);
            right = left + right;
            int bottom = diansvg.getIntrinsicHeight();
            int top = (int) (stopSecondY - diansvg.getIntrinsicHeight() / 2);
            bottom = top + bottom;
            diansvg.setBounds(left, top + 2, right, bottom + 2);
            diansvg.draw(canvas);
        } else {
            canvas.drawCircle(stopSecondX, stopSecondY, dianRadiu, mpaint2);
        }

    }

    private void drawBG(Canvas canvas) {
        if (bg == -1) {
            return;
        }
        long thetime = presstime;
        if (presstime > CHANGETIME) {
            thetime = CHANGETIME;
        }
        //y=0.17/200*x+0.83
        float rx = Math.min(canvas.getWidth() / 2, canvas.getWidth() / 2);
        int radius = (int) ((rx - (5 + dianRadiuOrgin * 2) * getResources().getDisplayMetrics().density) * (0.17 / 200 * thetime + 0.83));
        mpaintbg.setColor(bg);
        mpaintbg.setStrokeWidth(radius);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, mpaintbg);
    }

    private void drawSVG(Canvas canvas, Drawable drawable) {
        if (drawable == null) {
            return;
        }
        long thetime = presstime;
        if (presstime > CHANGETIME) {
            thetime = CHANGETIME;
        }
        double rate = (0.2 / CHANGETIME * thetime + 1.0);
//        Bitmap bitmap = Bitmap.createBitmap(svg.getIntrinsicWidth(), svg.getIntrinsicHeight(),
//                svg.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        int right = (int) (drawable.getIntrinsicWidth() * rate);
        int left = (canvas.getWidth() - ((int) (drawable.getIntrinsicWidth() * rate))) / 2;
        right = left + right;
        int bottom = (int) (drawable.getIntrinsicHeight() * rate);
        int top = (canvas.getHeight() - ((int) (drawable.getIntrinsicHeight() * rate))) / 2;
        bottom = top + bottom;
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (presstime == 0) {
                    isclicked = false;
                    startAnim();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_CANCEL:
                cancel();
                break;
            case MotionEvent.ACTION_UP:
                if (presstime != 0 && presstime < CLICKTIME) {
                    isclicked = true;
                    performClick();
                } else {
                    cancel();
                }
                break;
        }
        return true;
    }

    private void cancel() {
        reset();
        if (doActionListener != null) {
            doActionListener.doCancel(isclicked);
        }
    }

    public void reset() {
        presstime = 0;
        isdoaction = false;
        isdian = false;
        invalidate();
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    private void startAnim() {
        presstime++;
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        animator = ValueAnimator.ofInt(0, TIMETIME / JD);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                onpress((int) animation.getAnimatedValue());
            }
        });
        animator.setDuration(TIMETIME);
        animator.start();
    }

    private void onpress(int t) {
        if (presstime != t * JD) {
            presstime = t * JD;
            invalidate();
            if (presstime >= (TIMETIME - JD) && !isdoaction) {
                isdoaction = true;
                if (doActionListener != null) {
                    doActionListener.doAction(isclicked);
                }
            }
            if (presstime >= CHANGETIME && !isdian) {
                isdian = true;
                if (doActionListener != null) {
                    doActionListener.doChangeView(isclicked);
                }
            }
            if (t == TIMETIME / JD - 1) {
                reset();
            }
        }

    }

    DoActionListener doActionListener;

    public DoActionListener getDoActionListener() {
        return doActionListener;
    }

    public void setDoActionListener(DoActionListener doActionListener) {
        this.doActionListener = doActionListener;
    }

    public interface DoActionListener {
        void doChangeView(boolean clicked);

        void doAction(boolean clicked);

        void doCancel(boolean clicked);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    @Override
    protected void onDetachedFromWindowInternal() {
        super.onDetachedFromWindowInternal();
        cancel();
    }
}
