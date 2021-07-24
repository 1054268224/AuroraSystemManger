package com.worain.xproject.healthchartView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.worain.xproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/07/17
 *     desc   : ...
 * </pre>
 */

public class ChartView extends View {

    private static final int DEFAULT_BULE = 0XFF00BFFF;
    private static final int DEFAULT_GRAY = Color.GRAY;

    private final int backgroundColor;
    private int viewType; //视图种类
    private int minViewHeight; //控件的最低高度
    private final int minLineHeight;//折线最低的高度
    private final int lineInterval; //折线线段长度
    private float pointRadius; //折线点的半径
    private float textSize; //字体大小
    private float pointGap; //折线单位高度差
    private int horizontalPadding; //折线坐标图水平方向留出来的偏移量
    private int verticalPadding;//折线坐标图垂直方向留出来的偏移量
    private final int parallelNum;//平行线数量
    private int viewHeight;
    private int viewWidth;
    private int layoutWidth;//布局宽度
    private int screenHeight;

    private Paint linePaint; //线画笔
    private Paint textPaint; //文字画笔
    private Paint valuePaint;
    private float fontMetricsHeight;
    private Paint circlePaint; //圆点画笔
    private final int lineColor = DEFAULT_BULE; //折线颜色

    private List<ParmBean> datas = new ArrayList<>(); //元数据不可为空
    private List<ParmBean> anotherDatas = new ArrayList<>(); //元数据可为空
    private final List<PointF> points = new ArrayList<>(); //折线拐点的集合
    private float maxValue = -Float.MAX_VALUE;//元数据中的最高和最低值
    private float minValue = Float.MAX_VALUE;
    private int pointNum = -1;
    private float pointX;
    private float pointY;

    private VelocityTracker velocityTracker;
    private final Scroller scroller;
    private final ViewConfiguration viewConfiguration;

    private Axis axis;
    private Arrow arrow;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        scroller = new Scroller(context);
        viewConfiguration = ViewConfiguration.get(context);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HealthChartView);
        minLineHeight = (int) ta.getDimension(R.styleable.HealthChartView_min_point_height, dp2pxF(context, 150));
        lineInterval = (int) (ta.getDimension(R.styleable.HealthChartView_line_interval, dp2pxF(context, 40)));
        parallelNum = ta.getInt(R.styleable.HealthChartView_prarallel_num, 5);
        backgroundColor = ta.getColor(R.styleable.HealthChartView_background_color, getResources().getColor(R.color.white, null));
        ta.recycle();

        setBackgroundColor(backgroundColor);

        initSize(context);

        initPaint(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            viewHeight = Math.max(heightSize, minViewHeight);
        } else {
            viewHeight = minViewHeight;
        }
        int totalWidth = 0;
        if (datas.size() > 1) {
            totalWidth = 2 * horizontalPadding + lineInterval * (datas.size() - 1) + ((int) (pointRadius * 2));
        }
        viewWidth = Math.max(layoutWidth, totalWidth);  //默认控件最小宽度为屏幕宽度
        setMeasuredDimension(viewWidth, viewHeight);
        calculatePontGap();
    }

    int i = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        if (i == 0) {
            layoutWidth = ((View) getParent()).getMeasuredWidth();
            screenHeight = ((View) getParent()).getMeasuredHeight();
            i++;
        }
        super.onDraw(canvas);
        if (datas.isEmpty()) {
            return;
        }
        drawParallel(canvas);
        drawLine(canvas, datas, lineColor);
        drawValue(canvas);
        drawDots(canvas, datas);
        if (anotherDatas != null) {
            drawLine(canvas, anotherDatas, getContext().getColor(R.color.circle_purple));
            drawDots(canvas, anotherDatas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize(getContext());
        calculatePontGap();
    }

    private float lastX = 0;
    private float x = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {  //fling还没结束
                    scroller.abortAnimation();
                }
                lastX = x = event.getX();
                pointNum = (int) ((getScrollX() + x) / ((float) lineInterval));
                pointX = getScrollX() + x;
                pointY = event.getY();
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                int deltaX = (int) (lastX - x);
                if (getScrollX() + deltaX < 0) {    //越界恢复
                    scrollTo(0, 0);
                    return true;
                } else if (getScrollX() + deltaX > viewWidth - layoutWidth) {
                    scrollTo(viewWidth - layoutWidth, 0);
                    return true;
                }
                scrollBy(deltaX, 0);
                lastX = x;
                if (Math.abs(deltaX)>2)arrow.isVisible(true);
                break;
            case MotionEvent.ACTION_UP:
                x = event.getX();
                velocityTracker.computeCurrentVelocity(1000);  //计算1秒内滑动过多少像素
                int xVelocity = (int) velocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > viewConfiguration.getScaledMinimumFlingVelocity()) {  //滑动速度可被判定为抛动
                    scroller.fling(getScrollX(), 0, -xVelocity, 0, 0, viewWidth - layoutWidth, 0, 0);
                    invalidate();
                }
                arrow.isVisible(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 公开方法，用于设置元数据
     *
     * @param datas
     * @param axis
     */

    public void setDatas(List<ParmBean> datas, int viewType, Axis axis, Arrow arrow) {
        if (datas == null) {
            return;
        }
        this.datas = datas;
        this.axis = axis;
        this.arrow = arrow;
        this.viewType = viewType;
        notifydatasSetChanged(datas);
    }

    public List<ParmBean> getDatas() {
        return datas;
    }

    /**
     * 公开方法，用于设置元数据
     *
     * @param datas
     * @param anotherDatas
     * @param axis
     */

    public void setDatas(List<ParmBean> datas, List<ParmBean> anotherDatas, int viewType, Axis axis, Arrow arrow) {
        if (datas == null || anotherDatas == null) {
            return;
        }
        this.datas = datas;
        this.anotherDatas = anotherDatas;
        this.viewType = viewType;
        this.axis = axis;
        this.arrow = arrow;
        notifydatasSetChanged(datas);
        notifydatasSetChanged(anotherDatas);
    }

    public List<ParmBean> getAnotherDatas() {
        return anotherDatas;
    }

    public void notifydatasSetChanged(List<ParmBean> datas) {
        if (datas == null) {
            return;
        }
        points.clear();
        requestLayout();
        invalidate();
    }

    /**
     * 初始化默认数据
     */
    private void initSize(Context c) {
        horizontalPadding = dp2px(getContext(), 8);
        verticalPadding = (int) (7 * fontMetricsHeight); //根据x轴每一列字体数量确定乘数
        minViewHeight = minLineHeight + verticalPadding;
        pointRadius = dp2pxF(c, 3.5f);
        textSize = sp2pxF(c, 8);
    }

    /**
     * 计算折线单位高度差
     */
    private void calculatePontGap() {
        for (ParmBean bean : datas) {
            if (bean.getValue() > maxValue) {
                maxValue = bean.getValue();
            }
            if (bean.getValue() < minValue) {
                minValue = bean.getValue();
            }
        }

        for (ParmBean bean : anotherDatas) {
            if (bean.getValue() > maxValue) {
                maxValue = bean.getValue();
            }
            if (bean.getValue() < minValue) {
                minValue = bean.getValue();
            }
        }

        float gap = (maxValue - minValue) * 1.0f;
        gap = (gap == 0.0f ? 1.0f : gap);  //保证分母不为0
        pointGap = (viewHeight - verticalPadding) / (parallelNum) * (parallelNum - 2) / gap;
    }

    private void initPaint(Context c) {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(dp2px(c, 1));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        fontMetricsHeight = fontMetrics.descent - fontMetrics.ascent + 1;

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setTextSize(textSize);
        valuePaint.setColor(Color.BLACK);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStrokeWidth(dp2pxF(c, 1));
    }

    /**
     * 画平行线
     *
     * @param canvas
     */
    int index = 0;

    private void drawParallel(Canvas canvas) {
        canvas.save();
        Paint linePaint = new Paint();
        linePaint.reset();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp2px(getContext(), 1));
        linePaint.setColor(DEFAULT_GRAY);
        PathEffect pathEffect = new DashPathEffect(new float[]{5, 5, 5, 5}, 0);
        linePaint.setPathEffect(pathEffect);
        Path linePath = new Path();
        float y = (viewHeight - verticalPadding) / parallelNum;
        float[] tempY = new float[parallelNum];
        for (int i = 0; i < parallelNum; i++) {
            linePath.moveTo(horizontalPadding, ((y * (i + 1)) + pointRadius));
            linePath.lineTo(viewWidth - horizontalPadding, y * (i + 1));
            canvas.drawPath(linePath, linePaint);
            tempY[i] = y * (i + 1);
        }
        if (index == 0) {
            axis.onVertical(tempY, maxValue, (maxValue - minValue) / (parallelNum - 2), viewWidth - layoutWidth);
            index++;
        }
        float centerY = viewHeight - verticalPadding;
        float centerX;
        int temp = (int) (fontMetricsHeight / 2);

        //时间描述
        for (int i = 0; i < datas.size(); i++) {
            String month = datas.get(i).getTime().getMonth();
            String day = datas.get(i).getTime().getDay();
            String hour = datas.get(i).getTime().getHour();
            String min = datas.get(i).getTime().getMin();
            centerX = horizontalPadding + i * lineInterval + pointRadius;
            canvas.drawText(month, 0, month.length(), centerX, centerY + fontMetricsHeight, textPaint);
            canvas.drawText("/", 0, "/".length(), centerX, centerY + fontMetricsHeight * 2, textPaint);
            canvas.drawText(day, 0, day.length(), centerX, centerY + fontMetricsHeight * 3, textPaint);
            canvas.drawText(hour, 0, hour.length(), centerX, centerY + fontMetricsHeight * 4 + temp, textPaint);
            canvas.drawText(":", 0, ":".length(), centerX, centerY + fontMetricsHeight * 5 + temp, textPaint);
            canvas.drawText(min, 0, min.length(), centerX, centerY + fontMetricsHeight * 6 + temp, textPaint);
        }
        canvas.restore();
    }

    /**
     * 画折线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas, List<ParmBean> datas, int lineColor) {
        canvas.save();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(dp2pxF(getContext(), 1));
        linePaint.setStyle(Paint.Style.STROKE);
        Path linePath = new Path(); //用于绘制折线
        points.clear();
        float centerX;
        float centerY;
        valuePaint.setTextSize(2.5f * textSize); //字体放大一丢丢

        for (int i = 0; i < datas.size(); i++) {
            centerY = calculatePointY(datas.get(i));
            centerX = horizontalPadding + i * lineInterval + ((int) pointRadius);
            points.add(new PointF(centerX, centerY));

            if (i == 0) {
                linePath.moveTo(centerX, centerY);
            } else {
                linePath.lineTo(centerX, centerY);
            }
        }
        canvas.drawPath(linePath, linePaint); //画出折线
        canvas.restore();
    }

    private void drawValue(Canvas canvas) {
        List<ParmBean> aDatas = null;
        List<ParmBean> datas = null;
        float centerX;
        float centerY;
        float aDataY = -1;
        if (getDatas() != null) {
            datas = getDatas();
        }
        if (getAnotherDatas() != null) {
            aDatas = getAnotherDatas();
        }

        for (int i = 0; i < datas.size(); i++) {
            centerX = points.get(i).x;
            centerY = points.get(i).y;
            if (aDatas != null && aDatas.size() != 0) {
                aDataY = calculatePointY(aDatas.get(i));
            }
            if (pointNum != -1 && viewType != ParmBean.VIEW_TIRED
                    && viewType != ParmBean.VIEW_PRESSURE
                    && viewType != ParmBean.VIEW_HEALTH
                    && (Math.abs(pointX - centerX) < lineInterval / 2)
                    && (Math.abs(pointY - centerY) < lineInterval / 2
                    || Math.abs(pointY - aDataY) < lineInterval / 2)) {

                canvas.drawLine(centerX, (viewHeight - verticalPadding) / parallelNum, centerX, (viewHeight - verticalPadding), linePaint);
                drawStrValue(datas, pointNum, 2, centerX, centerY, canvas);
                if (aDatas != null && aDatas.size() != 0) {
                    drawStrValue(aDatas, pointNum, 1, centerX, aDataY, canvas);
                }

            }
        }
    }

    private int calculatePointY(ParmBean data) {
        int baseHeight = (int) (verticalPadding + (viewHeight - verticalPadding) / parallelNum * 0.5);
        return (int) (viewHeight - (baseHeight + (data.getValue() - minValue) * pointGap));
    }

    private void drawStrValue(List<ParmBean> datas, int pointNum, int valueType, float x, float y, Canvas canvas) {
        String str;
        float value = datas.get(pointNum).getValue();
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        if (viewType == ParmBean.VIEW_BLOODPRESSURE) {
            if (valueType == 1) {
                str = "收缩压：" + ((int) value);
            } else {
                str = "舒张压：" + ((int) value);
            }
        } else if (viewType == ParmBean.VIEW_RATE) {
            str = ((int) value) + "bpm";
        } else if (viewType == ParmBean.VIEW_SUGAR) {
            str = value + "mmol/L";
        } else {
            str = "";
        }

        if ((datas.size() - pointNum) < 4 && datas.size() > 5) {
            canvas.drawText(str, x - 95, y + (metrics.ascent + metrics.descent) * 3, valuePaint);
        } else {
            canvas.drawText(str, x + 95, y + (metrics.ascent + metrics.descent) * 3, valuePaint);
        }

    }

    /**
     * 画点
     *
     * @param canvas
     */
    private void drawDots(Canvas canvas, List<ParmBean> datas) {
        canvas.save();
        for (int i = 0; i < points.size(); i++) {
            //接下来画折线拐点的园,画一个颜色为背景颜色的实心园覆盖掉折线拐角
            circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            setCircleColor(viewType, datas.get(i).getColorType());
            canvas.drawCircle(points.get(i).x, points.get(i).y,
                    pointRadius,
                    circlePaint);
        }
        textPaint.setTextSize(textSize);
        canvas.restore();
    }

    private void setCircleColor(int viewType, int value) {
        int green = R.color.circle_green;
        int red = R.color.circle_red;
        int yellow = R.color.circle_yellow;
        int purple = R.color.circle_purple;
        if (viewType == ParmBean.VIEW_BLOODPRESSURE) {
            //血压 2 红 3 紫 其它 绿色
            if (value == 2) {
                color(red);
            } else if (value == 3) {
                color(purple);
            } else {
                color(green);
            }
        } else if (viewType == ParmBean.VIEW_RATE) {
            //心率 3 橙 4红 其它 绿
            if (value == 3) {
                color(yellow);
            } else if (value == 4) {
                color(red);
            } else {
                color(green);
            }
        } else if (viewType == ParmBean.VIEW_SUGAR) {
            //血糖 过高1 红   过低2 紫  尚可3 橙  良好4 绿
            if (value == 1) {
                color(red);
            } else if (value == 2) {
                color(purple);
            } else if (value == 3) {
                color(yellow);
            } else if (value == 4) {
                color(green);
            }
        } else if (viewType == ParmBean.VIEW_TIRED) {
            //交感神经 23 橙 45 红 其它 绿
            if (value == 3 || value == 2) {
                color(yellow);
            } else if (value == 4 || value == 5) {
                color(red);
            } else {
                color(green);
            }
        } else if (viewType == ParmBean.VIEW_PRESSURE) {
            //压力指数 2橙 3 红 其它 绿
            if (value == 2) {
                color(yellow);
            } else if (value == 3) {
                color(red);
            } else {
                color(green);
            }
        } else if (viewType == ParmBean.VIEW_HEALTH) {
            //健康指数 3 橙 12 红 其它 绿
            if (value == 3) {
                color(yellow);
            } else if (value == 1 || value == 2) {
                color(red);
            } else {
                color(green);
            }
        }
    }

    private void color(int res) {
        circlePaint.setColor(getResources().getColor(res, null));
    }

    //工具类
    public static int dp2px(Context c, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context c, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }

    public static float dp2pxF(Context c, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static float sp2pxF(Context c, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }

    public interface Axis {
        void onVertical(float[] yAxis, float max, float lineAxisSize, int viewWidth);
    }

    public interface Arrow {
        void isVisible(boolean bool);
    }
}
