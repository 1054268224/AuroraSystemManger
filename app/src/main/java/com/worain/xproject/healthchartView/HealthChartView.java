package com.worain.xproject.healthchartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.worain.xproject.R;

import java.util.List;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/07/17
 *     desc   : ...
 * </pre>
 */
public class HealthChartView extends RelativeLayout {

    private ChartView chartView;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private int viewType;
    private Axis axis;
    private Arrow arrow;

    public HealthChartView(Context context) {
        super(context);
    }

    public HealthChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.health_chart_view, this);
        axis = new Axis();
        arrow = new Arrow();
        setWillNotDraw(false);
        initView();
    }

    public HealthChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (axis.getyAxis() != null) {
            drawVerticalAxisValues(axis.getyAxis(), axis.getLineAxisSize(), axis.getMax(), canvas);
        }
    }

    private class Axis implements ChartView.Axis {
        float[] yAxis;
        float lineAxisSize;
        float max;
        int viewWidth;

        @Override
        public void onVertical(float[] yAxis, float max, float lineAxisSize, int viewWidth) {
            this.yAxis = yAxis;
            this.lineAxisSize = lineAxisSize;
            this.max = max;
            this.viewWidth = viewWidth;
            chartView.scrollTo(viewWidth, 0);
            invalidate();
        }

        public float[] getyAxis() {
            return yAxis;
        }

        public float getLineAxisSize() {
            return lineAxisSize;
        }

        public float getMax() {
            return max;
        }

        public int getViewWidth() {
            return viewWidth;
        }
    }

    private class Arrow implements ChartView.Arrow {

        @Override
        public void isVisible(boolean bool) {
//            if (bool) {
//                mIvLeft.setVisibility(VISIBLE);
//                mIvRight.setVisibility(VISIBLE);
//            } else {
//                mIvLeft.postDelayed(() -> {
//                    mIvLeft.setVisibility(GONE);
//                }, 3500);
//                mIvRight.postDelayed(() -> {
//                    mIvRight.setVisibility(GONE);
//                }, 3500);
//            }
        }
    }

    public List<ParmBean> getDatas() {
        return chartView.getDatas();
    }

    public void setDatas(List<ParmBean> datas, int viewType) {
        this.viewType = viewType;
        chartView.setDatas(datas, viewType, axis, arrow);
    }

    public List<ParmBean> getAnotherDatas() {
        return chartView.getAnotherDatas();
    }

    public void setDatas(List<ParmBean> datas, List<ParmBean> anotherDatas, int viewType) {
        this.viewType = viewType;
        chartView.setDatas(datas, anotherDatas, viewType, axis, arrow);
    }

    private void initView() {
        chartView = findViewById(R.id.chart_view);
        mIvLeft = findViewById(R.id.iv_left);
        mIvRight = findViewById(R.id.iv_right);
        mIvLeft.setOnClickListener(v -> {
            chartView.scrollBy(-200, 0);
            if (chartView.getScrollX() < 0) {
                chartView.scrollTo(1, 0);
            }
        });
        mIvRight.setOnClickListener(v -> {
            chartView.scrollBy(200, 0);
            if (chartView.getScrollX() > axis.getViewWidth()) {
                chartView.scrollTo(axis.getViewWidth(), 0);
            }
        });

    }

    private void drawVerticalAxisValues(float[] y, float lineAxisSize, float max, Canvas canvas) {
        canvas.save();
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if (viewType == ParmBean.VIEW_BLOODPRESSURE || viewType == ParmBean.VIEW_RATE || viewType == ParmBean.VIEW_SUGAR) {
            for (int i = 0; i < y.length; i++) {
                int value = (int) (max + lineAxisSize * 0.5 - lineAxisSize * i);
                Paint.FontMetrics metrics = textPaint.getFontMetrics();
                canvas.drawText(String.valueOf(value),
                        25,
                        y[i] + (metrics.descent - metrics.ascent) / 2,
                        textPaint);
            }
        } else if (viewType == ParmBean.VIEW_TIRED) {
            for (int i = 0; i < y.length; i++) {
                Paint.FontMetrics metrics = textPaint.getFontMetrics();
                String str = "";
                if (i == 1) {
                    str = "亢奋";
                } else if (i == 2) {
                    str = "稳定";
                } else if (i == 3) {
                    str = "疲劳";
                } else {
                    str = "";
                }
                canvas.drawText(str, 25, y[i] + (metrics.descent - metrics.ascent) / 2, textPaint);
            }
        } else if (viewType == ParmBean.VIEW_PRESSURE) {
            for (int i = 0; i < y.length; i++) {
                Paint.FontMetrics metrics = textPaint.getFontMetrics();
                String str = "";
                if (i == 1) {
                    str = "高";
                } else if (i == 2) {
                    str = "中";
                } else if (i == 3) {
                    str = "低";
                } else {
                    str = "";
                }
                canvas.drawText(str, 25, y[i] + (metrics.descent - metrics.ascent) / 2, textPaint);
            }
        } else if (viewType == ParmBean.VIEW_HEALTH) {
            for (int i = 0; i < y.length; i++) {
                Paint.FontMetrics metrics = textPaint.getFontMetrics();
                int value = (int) (max + lineAxisSize * 0.5 - lineAxisSize * i);
                if (value > 100) {
                    value = 100;
                } else if (value < 0) {
                    value = 0;
                }
                canvas.drawText(String.valueOf(value), 25, y[i] + (metrics.descent - metrics.ascent) / 2, textPaint);
            }
        }
        canvas.restore();
    }

}
