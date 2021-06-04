package com.example.systemmanageruidemo.trafficmonitor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.systemmanageruidemo.R;
import com.example.systemmanageruidemo.contrarywind.adapter.WheelAdapter;
import com.example.systemmanageruidemo.contrarywind.listener.OnItemSelectedListener;
import com.example.systemmanageruidemo.contrarywind.view.WheelView;
import com.example.systemmanageruidemo.view.CustomEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TraSettingDialog extends Dialog {
    private final static int ALERT = 0;
    private final static int CLOSE = 1;
    private Context context;
    private Object data;
    private TextView mTvTitle;
    private RelativeLayout mRlBtnView;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private String title;
    private String strCancel;
    private String strConfirm;

    private WheelView mWheelView;
    private int mViewType = -1;
    private int mFlowStartDay = 0;

    private RelativeLayout mRlFlowEdit;
    private CustomEditText mEdFlow;
    private String mFlowResult;
    private boolean isUnitGB = true;
    private TextView mTvUnitMB;
    private TextView mTvUnitGB;

    private LinearLayout mLlFlowRunOut;
    private RelativeLayout mRlFlowAlert;
    private CheckBox mCbAlert;
    private RelativeLayout mRlFlowClose;
    private CheckBox mCbClose;

    public TraSettingDialog(Context context, int viewType, String title, String strCancel, String strConfirm, Object data) {
        super(context);
        this.context = context;
        mViewType = viewType;
        this.title = title;
        this.strCancel = strCancel;
        this.strConfirm = strConfirm;
        this.data = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_traffic_setting);
        initView(mViewType);
        refreshView();
        initEvent();
    }

    private void initView(int viewType) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mRlBtnView = (RelativeLayout) findViewById(R.id.rl_btn_view);
        mTvConfirm = (TextView) findViewById(R.id.tv_positive);
        mTvCancel = (TextView) findViewById(R.id.tv_negtive);
        if (viewType == 0) {
            mWheelView = (WheelView) findViewById(R.id.wv);
            mWheelView.setVisibility(View.VISIBLE);
            initWheel(mWheelView, getNumberList(32, 1, 1), context, 20, 5);
            mWheelView.setOnItemSelectedListener(index -> mFlowStartDay = index);
        } else if (viewType == 1 || viewType == 2 || viewType == 3) {
            mRlFlowEdit = (RelativeLayout) findViewById(R.id.rl_flow_edit);
            mEdFlow = (CustomEditText) findViewById(R.id.et_flow);
            mTvUnitMB = (TextView) findViewById(R.id.tv_unit_mb);
            mTvUnitGB = (TextView) findViewById(R.id.tv_unit_gb);
            mRlFlowEdit.setVisibility(View.VISIBLE);
            mEdFlow.setOnEditListener(strEdit -> mFlowResult = strEdit);
        } else if (viewType == 4) {
            getWindow().getDecorView().setBackgroundResource(R.drawable.select_dialog_bg_wyg_ui);
            mLlFlowRunOut = (LinearLayout) findViewById(R.id.ll_flow_run_out);
            mRlFlowAlert = (RelativeLayout) findViewById(R.id.rl_flow_alert);
            mCbAlert = (CheckBox) findViewById(R.id.cb_alert);
            mRlFlowClose = (RelativeLayout) findViewById(R.id.rl_flow_close);
            mCbClose = (CheckBox) findViewById(R.id.cb_close);
            mLlFlowRunOut.setVisibility(View.VISIBLE);
            clickItem((Integer) data);
        }
    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    private void refreshView() {
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
            mTvTitle.setVisibility(View.VISIBLE);
        } else {
            mTvTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(strCancel)) {
            mTvCancel.setText(strCancel);
            mTvCancel.setVisibility(View.VISIBLE);
        } else {
            mTvCancel.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(strConfirm)) {
            mTvConfirm.setText(strConfirm);
            mTvConfirm.setVisibility(View.VISIBLE);
        } else {
            mTvConfirm.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(strCancel) && TextUtils.isEmpty(strConfirm)) {
            mRlBtnView.setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        mTvConfirm.setOnClickListener(v -> {
            if (onClickBottomListener != null) {
                if (mViewType == 0) {
                    onClickBottomListener.onConfirmClick(mViewType, "" + mFlowStartDay, mFlowStartDay);
                } else if (mViewType == 1 || mViewType == 2 || mViewType == 3) {
                    if (isUnitGB == true) {
                        onClickBottomListener.onConfirmClick(mViewType, mFlowResult + "GB", mFlowResult);
                    } else {
                        onClickBottomListener.onConfirmClick(mViewType, mFlowResult + "MB", mFlowResult);
                    }
                }

            }
        });
        mTvCancel.setOnClickListener(v -> {
            if (onClickBottomListener != null) {
                onClickBottomListener.onCancelClick(mViewType);
            }
        });
        if (mViewType == 1 || mViewType == 2 || mViewType == 3) {
            mTvUnitMB.setOnClickListener(v -> {
                setTvColor(mTvUnitMB, R.drawable.bg_tv_blue, R.color.white);
                setTvColor(mTvUnitGB, R.drawable.bg_tv_gray, R.color.item_gray);
                isUnitGB = false;
            });
            mTvUnitGB.setOnClickListener(v -> {
                setTvColor(mTvUnitGB, R.drawable.bg_tv_blue, R.color.white);
                setTvColor(mTvUnitMB, R.drawable.bg_tv_gray, R.color.item_gray);
                isUnitGB = true;
            });
        } else if (mViewType == 4) {
            mRlFlowAlert.setOnClickListener(v -> {
                clickItem(ALERT);
            });
            mRlFlowClose.setOnClickListener(v -> {
                clickItem(CLOSE);
            });
        }
    }

    private void clickItem(int index) {
        if (index == 0) {
            mCbAlert.setChecked(true);
            mCbClose.setChecked(false);
        } else {
            mCbAlert.setChecked(false);
            mCbClose.setChecked(true);
        }
        onClickItemListener.onItemClick(mViewType, index);
    }

    private void setTvColor(TextView tv, int bgRes, int textRes) {
        tv.setBackground(context.getResources().getDrawable(bgRes, null));
        tv.setTextColor(context.getColor(textRes));
    }

    public interface OnClickBottomListener {
        void onCancelClick(int viewType);

        void onConfirmClick(int viewType, String result, Object resultValue);
    }

    public OnClickBottomListener onClickBottomListener;

    public TraSettingDialog setOnClickBottomListener(OnClickBottomListener listener) {
        onClickBottomListener = listener;
        return this;
    }

    public interface OnClickItemListener {
        void onItemClick(int viewType, int index);
    }

    public OnClickItemListener onClickItemListener;

    public TraSettingDialog setOnClickItemListener(OnClickItemListener listener) {
        onClickItemListener = listener;
        return this;
    }

    public void initWheel(final WheelView myWheelView, List list, Context context, int size, int showcount) {
//        myWheelView.setDividerColor(context.getColor(R.color.transparent));
        myWheelView.setTextColorCenter(context.getColor(R.color.colorPrimary_wyh_traf));
        myWheelView.setTextColorOut(context.getColor(R.color.static_newui_tab));
        myWheelView.setTextSize(size);
        myWheelView.setAlphaGradient(false);
        myWheelView.setItemsVisibleCount(showcount);
        myWheelView.setAlphaGradient(true);
        myWheelView.setTextXOffset(0);
        myWheelView.setCurrentItem((Integer) data);
        myWheelView.setDividerWidth(1);
        myWheelView.setDividerType(WheelView.DividerType.WRAP);
        myWheelView.setAdapter(new SimpleArrayWheelAdapter(list));
    }

    public static class SimpleArrayWheelAdapter implements WheelAdapter {
        List mlist;

        SimpleArrayWheelAdapter(List list) {
            mlist = list;
        }

        @Override
        public int getItemsCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int index) {
            return mlist.get(index);
        }

        @Override
        public int indexOf(Object o) {
            int i = 0;
            for (Object o1 : mlist) {
                if (o1.equals(o)) {
                    return i;
                }
                i++;
            }
            return 0;
        }
    }

    public static DecimalFormat df = new DecimalFormat("00");

    public static List<String> getNumberList(int max, int level, int init) {
        List list = new ArrayList();

        for (int i = init; i < max; i = i + level) {
            list.add(df.format(i));
        }
        return list;
    }

}
