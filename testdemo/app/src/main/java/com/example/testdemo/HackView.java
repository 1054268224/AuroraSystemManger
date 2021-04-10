package com.example.testdemo;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HackView extends View{
    private Paint mPaint;
    /** 控件的宽 */
    private int mWidth;
    /** 控件的高 */
    private int mHeight;
    /** 所有字母 */
    private static final String[] CODES = {
            "A","B","C","D","E","F","G","H","I","J","K",
            "L","M","N","O","P","Q","R","S","T","U","V",
            "W","K","Y","Z"
    };

    private static final int WHAT = 1;
    /** 所有的HackLine组合 */
    private List<HackLine> mHackLines = new ArrayList<HackLine>();

    private WeakHandler mHandler;

    public HackView(Context context) {
        this(context,null);
    }
    public HackView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public HackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mHandler = new WeakHandler((Activity) context);
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
                    case WHAT:
                        nextPlay(dip2px(getContext(), 20));
                        for (int i = 0; i < mHackLines.size(); i++) {
                            if(mHackLines.get(i).p.y >= mHeight/2*3){
                                addHackLine(mHackLines.get(i));
                            }
                        }
                        invalidate();
                        break;
                }
            }
        }
    }
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(dip2px(getContext(), 20));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(dip2px(getContext(), 5));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();//获取控件宽高
        mHeight = getMeasuredHeight();
        mHackLines.clear();//清空集合
        initPlayData();
    }
    /**
     * 下一帧播放
     * @param Nnum 每次下移多远 距离
     */
    public void nextPlay(int Nnum){
        for (int i = 0; i < mHackLines.size(); i++) {
            List<HackCode> hcs = mHackLines.get(i).hcs;
            hcs.clear();
            mHackLines.get(i).p.y+=Nnum;
            for (int j = 0; j < 1; j++) {
                HackCode hc = new HackCode();
                hc.alpha -= 30*j;
                hc.code = CODES[new Random().nextInt(CODES.length)];
                hc.p.x = mHackLines.get(i).p.x;
                hc.p.y = mHackLines.get(i).p.y-dip2px(getContext(), 25)*j;
                hcs.add(hc);
            }
        }
    }
    /**
     * 删除一列 同时添加初始化一列
     * @param hackLine
     */
    public void addHackLine(HackLine hackLine){
        if(hackLine == null){
            return;
        }
        int num = hackLine.NUM;
        mHackLines.remove(hackLine);//如果存在 删除  重新添加

        HackLine hl;
        hl= new HackLine();
        hl.p.x = mWidth/9*(num-1);
        hl.p.y = mHeight/12*(7-(num-1));
        for (int j = 0; j < 1; j++) {
            HackCode hc = new HackCode();
            hc.alpha -= 30*j;
            hc.code = CODES[new Random().nextInt(CODES.length)];
            hc.p.x = hl.p.x;
            hc.p.y = hl.p.y-dip2px(getContext(), 25)*j;
            hl.hcs.add(hc);
        }
        hl.NUM = num;
        mHackLines.add(hl);
    }
    /**
     * 初始化每一行数据
     * @param x
     * @param y
     */
    public void initHackLine(int x,int y){
        HackLine hl;
        for (int i = 0; i < 9; i++) {
            hl= new HackLine();
            hl.p.x = x*i;
            hl.p.y = y*(7-i);
            for (int j = 0; j < 1; j++) {
                HackCode hc = new HackCode();
                hc.alpha -= 30*j;
                hc.code = CODES[new Random().nextInt(CODES.length)];
                hc.p.x = hl.p.x;
                hc.p.y = hl.p.y-dip2px(getContext(), 25)*j;
                hl.hcs.add(hc);
            }
            mHackLines.add(hl);
            hl.NUM = mHackLines.size();
        }
    }
    /**
     * 初始化播放数据
     */
    public void initPlayData(){
        initHackLine(mWidth/9, mHeight/12);
        initHackLine(mWidth/9, mHeight/7);
        HackLine hl;
        for (int i = 3; i < 9; i++) {
            hl= new HackLine();
            hl.p.x = mWidth/9*(i+1);
            hl.p.y = mHeight/7*(9-i);
            for (int j = 0; j < 1; j++) {
                HackCode hc = new HackCode();
                hc.alpha -= 30*j;
                hc.code = CODES[new Random().nextInt(CODES.length)];
                hc.p.x = hl.p.x;
                hc.p.y = hl.p.y-dip2px(getContext(), 25)*j;
                hl.hcs.add(hc);
            }
            mHackLines.add(hl);
            hl.NUM = mHackLines.size();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mHackLines.size(); i++) {
            drawText(i, canvas);
        }
        mHandler.sendEmptyMessageDelayed(WHAT, 100);
    }

    public void drawText(int nindex,Canvas canvas){
        HackLine hackLine = mHackLines.get(nindex);
        for (int i = 0; i < hackLine.hcs.size(); i++) {
            HackCode hackCode = hackLine.hcs.get(i);
            mPaint.setAlpha(hackCode.alpha);
            canvas.drawText(hackCode.code, hackCode.p.x, hackCode.p.y, mPaint);
        }
    }
    /**
     * 每条线 包含多个字母
     **/
    class HackLine{
        public int NUM = 0;//用于记录这列的标示
        private Point p = new Point();//线的初始位置
        List<HackCode> hcs = new ArrayList<HackView.HackCode>();//黑客字母的一条线
    }
    /**
     * 每个字母
     */
    class HackCode{
        Point p = new Point();//每一个字母的坐标
        int alpha = 255;//透明度值 默认255
        String code = "A";//字母的值
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);//停止刷新
    }
    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

